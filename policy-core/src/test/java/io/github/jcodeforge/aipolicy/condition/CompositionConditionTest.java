package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class CompositionConditionTest {

    private final ActionContext userInitiatedContext = new ActionContext("customer.delete",
                    "com.example.agent", true);

    private final ActionContext nonUserInitiatedContext =
            new ActionContext("customer.delete", "com.example.agent", false);

    private final ActionContext differentCallerContext = new ActionContext("customer.delete",
            "com.example.other", true);

    // -------------------------------------------------------------------------
    // AndCondition
    // -------------------------------------------------------------------------

    @Test
    public void andConditionMatchesWhenAllConditionsMatch() {
        PolicyCondition condition = new AndCondition(
                new CallerCondition("com.example.agent"),
                new UserInitiatedCondition());

        assertTrue(condition.matches(userInitiatedContext));
    }

    @Test
    public void andConditionDoesNotMatchWhenOneConditionDoesNotMatch() {
        PolicyCondition condition = new AndCondition(new CallerCondition("com.example.agent"),
                new UserInitiatedCondition());

        assertFalse(condition.matches(nonUserInitiatedContext));
    }

    @Test
    public void andConditionDoesNotMatchWhenAnotherConditionDoesNotMatch() {
        PolicyCondition condition = new AndCondition(new CallerCondition("com.example.agent"),
                new UserInitiatedCondition());

        assertFalse(condition.matches(differentCallerContext));
    }

    @Test
    public void andConditionMatchesSingleCondition() {
        PolicyCondition condition = new AndCondition(new UserInitiatedCondition());

        assertTrue(condition.matches(userInitiatedContext));
    }

    @Test(expected = IllegalArgumentException.class)
    public void andConditionRequiresAtLeastOneCondition() {
        new AndCondition();
    }

    @Test(expected = NullPointerException.class)
    public void andConditionDoesNotAllowNullConditionArray() {
        new AndCondition((PolicyCondition[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void andConditionDoesNotAllowNullCondition() {
        new AndCondition(new UserInitiatedCondition(), null);
    }

    @Test(expected = NullPointerException.class)
    public void andConditionRequiresContext() {
        PolicyCondition condition = new AndCondition(new UserInitiatedCondition());
        condition.matches(null);
    }

    // -------------------------------------------------------------------------
    // OrCondition
    // -------------------------------------------------------------------------

    @Test
    public void orConditionMatchesWhenOneConditionMatches() {
        PolicyCondition condition = new OrCondition(new CallerCondition("com.example.agent"),
                new UserInitiatedCondition());

        assertTrue(condition.matches(differentCallerContext));
    }

    @Test
    public void orConditionMatchesWhenAllConditionsMatch() {
        PolicyCondition condition = new OrCondition(new CallerCondition("com.example.agent"),
                new UserInitiatedCondition());

        assertTrue(condition.matches(userInitiatedContext));
    }

    @Test
    public void orConditionDoesNotMatchWhenNoConditionMatches() {
        PolicyCondition condition = new OrCondition(new CallerCondition("com.example.trusted"),
                new UserInitiatedCondition());

        assertFalse(condition.matches(nonUserInitiatedContext));
    }

    @Test
    public void orConditionMatchesSingleCondition() {
        PolicyCondition condition = new OrCondition(new UserInitiatedCondition());

        assertTrue(condition.matches(userInitiatedContext));
    }

    @Test(expected = IllegalArgumentException.class)
    public void orConditionRequiresAtLeastOneCondition() {
        new OrCondition();
    }

    @Test(expected = NullPointerException.class)
    public void orConditionDoesNotAllowNullConditionArray() {
        new OrCondition((PolicyCondition[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void orConditionDoesNotAllowNullCondition() {
        new OrCondition(new UserInitiatedCondition(), null);
    }

    @Test(expected = NullPointerException.class)
    public void orConditionRequiresContext() {
        PolicyCondition condition = new OrCondition(new UserInitiatedCondition());
        condition.matches(null);
    }

    // -------------------------------------------------------------------------
    // NotCondition
    // -------------------------------------------------------------------------

    @Test
    public void notConditionInvertsMatchingCondition() {
        PolicyCondition condition = new NotCondition(new UserInitiatedCondition());

        assertFalse(condition.matches(userInitiatedContext));
    }

    @Test
    public void notConditionInvertsNonMatchingCondition() {
        PolicyCondition condition = new NotCondition(new UserInitiatedCondition());

        assertTrue(condition.matches(nonUserInitiatedContext));
    }

    @Test
    public void notConditionCanWrapCallerCondition() {
        PolicyCondition condition = new NotCondition(new CallerCondition("com.example.agent"));

        assertTrue(condition.matches(differentCallerContext));
    }

    @Test(expected = NullPointerException.class)
    public void notConditionDoesNotAllowNullCondition() {
        new NotCondition(null);
    }

    @Test(expected = NullPointerException.class)
    public void notConditionRequiresContext() {
        PolicyCondition condition = new NotCondition(new UserInitiatedCondition());
        condition.matches(null);
    }
}