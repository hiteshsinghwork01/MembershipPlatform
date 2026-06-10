package com.firstclub.membership;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.membership.client.OrderClient;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.response.OrderStatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration tests: real Spring context, real H2 DB, real Liquibase seed data.
 * OrderClient (external HTTP) is mocked so tier assignment is deterministic.
 *
 * Seed data summary (from 002-seed-data.sql):
 *   Users  : 1=Rahul(REGULAR)  2=Priya(VIP)  3=Amit(NEW)
 *   Plans  : 1=Monthly  2=Quarterly  3=Yearly
 *   Tiers  : 1=Silver(lvl1)  2=Gold(lvl2)  3=Platinum(lvl3)
 *
 * Tier eligibility (OR logic on all tiers):
 *   Silver    : orderCount >= 1  (ALL_TIME)
 *   Gold      : orderCount >= 10 (MONTHLY)  OR  orderValue >= 5000  (MONTHLY)
 *   Platinum  : orderValue >= 15000 (MONTHLY)  OR  cohort=VIP  OR  cohort=PREMIUM
 *
 * @Transactional on each test rolls back DB changes; seed data persists across tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MembershipIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  OrderClient orderClient;

    // ── Seeded IDs ────────────────────────────────────────────────────────────
    static final long USER_REGULAR  = 1L;
    static final long USER_VIP      = 2L;
    static final long USER_NEW      = 3L;
    static final long PLAN_MONTHLY  = 1L;
    static final long TIER_SILVER   = 1L;
    static final long TIER_GOLD     = 2L;
    static final long TIER_PLATINUM = 3L;

    @BeforeEach
    void stubOrderClient() {
        // Default: 5 orders, ₹1000 — qualifies only for Silver
        stubOrders(5, "1000.00");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Subscription — happy paths
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void subscribe_regularUser_getsSilver() throws Exception {
        mockMvc.perform(post("/api/v1/memberships/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscribeJson(USER_REGULAR, PLAN_MONTHLY)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.userId").value(USER_REGULAR))
                .andExpect(jsonPath("$.data.tier.id").value(TIER_SILVER))
                .andExpect(jsonPath("$.data.plan.id").value(PLAN_MONTHLY))
                .andExpect(jsonPath("$.data.expiryDate").isNotEmpty());
    }

    @Test
    void subscribe_vipUser_getsPlatinum() throws Exception {
        // VIP cohort satisfies the USER_COHORT EQ VIP criterion on Platinum
        mockMvc.perform(post("/api/v1/memberships/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscribeJson(USER_VIP, PLAN_MONTHLY)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.tier.id").value(TIER_PLATINUM));
    }

    @Test
    void subscribe_highOrderStats_getsGold() throws Exception {
        stubOrders(15, "6000.00"); // qualifies for Gold
        mockMvc.perform(post("/api/v1/memberships/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscribeJson(USER_REGULAR, PLAN_MONTHLY)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.tier.id").value(TIER_GOLD));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Subscription — error cases
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void subscribe_twice_returns409() throws Exception {
        subscribe(USER_REGULAR, PLAN_MONTHLY);

        mockMvc.perform(post("/api/v1/memberships/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscribeJson(USER_REGULAR, PLAN_MONTHLY)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void subscribe_unknownPlan_returns404() throws Exception {
        mockMvc.perform(post("/api/v1/memberships/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscribeJson(USER_REGULAR, 999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    void subscribe_missingUserId_returns400WithValidationMessage() throws Exception {
        mockMvc.perform(post("/api/v1/memberships/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"planId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("User ID is required")));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Get active membership
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void getActiveMembership_returnsCurrentSubscription() throws Exception {
        subscribe(USER_REGULAR, PLAN_MONTHLY);

        mockMvc.perform(get("/api/v1/memberships/user/{userId}", USER_REGULAR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.userId").value(USER_REGULAR));
    }

    @Test
    void getActiveMembership_whenNone_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/memberships/user/{userId}", USER_REGULAR))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Tier upgrade
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void upgrade_promotesToHigherTierWhenNowEligible() throws Exception {
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY); // Silver

        stubOrders(15, "6000.00"); // now eligible for Gold

        mockMvc.perform(put("/api/v1/memberships/{id}/upgrade", membershipId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.upgraded").value(true))
                .andExpect(jsonPath("$.data.membershipResponse.tier.id").value(TIER_GOLD));
    }

    @Test
    void upgrade_noChangeWhenAlreadyAtHighestEligibleTier() throws Exception {
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY); // Silver, stats stay low

        mockMvc.perform(put("/api/v1/memberships/{id}/upgrade", membershipId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.upgraded").value(false))
                .andExpect(jsonPath("$.data.message").value("Not eligible for upgrade"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Tier downgrade
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void downgrade_demotesWhenNoLongerEligibleForCurrentTier() throws Exception {
        stubOrders(15, "6000.00");
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY); // Gold

        stubOrders(3, "500.00"); // no longer Gold-eligible

        mockMvc.perform(put("/api/v1/memberships/{id}/downgrade", membershipId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tier.id").value(TIER_SILVER));
    }

    @Test
    void downgrade_failsWhenStillEligibleForCurrentTier() throws Exception {
        stubOrders(15, "6000.00");
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY); // Gold, stats unchanged

        mockMvc.perform(put("/api/v1/memberships/{id}/downgrade", membershipId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Unsubscribe
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void unsubscribe_cancelsMembership() throws Exception {
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY);

        mockMvc.perform(put("/api/v1/memberships/{id}/unsubscribe", membershipId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Too expensive\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"))
                .andExpect(jsonPath("$.data.cancellationReason").value("Too expensive"));
    }

    @Test
    void unsubscribe_thenGetActive_returns404() throws Exception {
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY);

        mockMvc.perform(put("/api/v1/memberships/{id}/unsubscribe", membershipId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/memberships/user/{userId}", USER_REGULAR))
                .andExpect(status().isNotFound());
    }

    @Test
    void unsubscribe_alreadyCancelled_returns400() throws Exception {
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY);

        mockMvc.perform(put("/api/v1/memberships/{id}/unsubscribe", membershipId))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/memberships/{id}/unsubscribe", membershipId))
                .andExpect(status().isBadRequest());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Change history
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void changeHistory_recordsSubscribeEvent() throws Exception {
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY);

        mockMvc.perform(get("/api/v1/memberships/{id}/history", membershipId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].changeType").value("SUBSCRIBED"));
    }

    @Test
    void changeHistory_recordsUpgradeEvent() throws Exception {
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY);
        stubOrders(15, "6000.00");
        mockMvc.perform(put("/api/v1/memberships/{id}/upgrade", membershipId)).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/memberships/{id}/history", membershipId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].changeType", hasItems("SUBSCRIBED", "UPGRADED")));
    }

    @Test
    void changeHistory_recordsCancelEvent() throws Exception {
        long membershipId = subscribe(USER_REGULAR, PLAN_MONTHLY);
        mockMvc.perform(put("/api/v1/memberships/{id}/unsubscribe", membershipId)).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/memberships/{id}/history", membershipId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].changeType", hasItems("SUBSCRIBED", "CANCELLED")));
    }

    @Test
    void changeHistory_unknownMembership_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/memberships/{id}/history", 999L))
                .andExpect(status().isNotFound());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // User registration
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void registerUser_createsUserSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "New Member",
                                  "email": "newmember@example.com",
                                  "phoneNumber": "9000000001",
                                  "cohort": "REGULAR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("newmember@example.com"))
                .andExpect(jsonPath("$.data.cohort").value("REGULAR"));
    }

    @Test
    void registerUser_duplicateEmail_returns400() throws Exception {
        // UserServiceImpl does an explicit existsByEmail check → MembershipException → 400
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Duplicate",
                                  "email": "rahul@example.com",
                                  "phoneNumber": "9000000002"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void registerUser_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Bad Email",
                                  "email": "not-an-email",
                                  "phoneNumber": "9000000003"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Plan management
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void getPlans_returnsAllSeededPlans() throws Exception {
        mockMvc.perform(get("/api/v1/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(3)));
    }

    @Test
    void deactivatePlan_failsWhenActiveSubscribersExist() throws Exception {
        subscribe(USER_REGULAR, PLAN_MONTHLY);

        mockMvc.perform(delete("/api/v1/plans/{planId}", PLAN_MONTHLY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deactivatePlan_succeedsWhenNoActiveSubscribers() throws Exception {
        mockMvc.perform(delete("/api/v1/plans/{planId}", PLAN_MONTHLY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Helpers
    // ═══════════════════════════════════════════════════════════════════════════

    /** Subscribes a user and returns the created membership ID. */
    private long subscribe(long userId, long planId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/memberships/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscribeJson(userId, planId)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    private String subscribeJson(long userId, long planId) throws Exception {
        SubscribeRequest req = new SubscribeRequest();
        req.setUserId(userId);
        req.setPlanId(planId);
        return objectMapper.writeValueAsString(req);
    }

    private void stubOrders(long count, String value) {
        when(orderClient.getOrderStats(anyLong(), any()))
                .thenReturn(OrderStatsResponse.builder()
                        .orderCount(count)
                        .totalOrderValue(new BigDecimal(value))
                        .build());
    }
}
