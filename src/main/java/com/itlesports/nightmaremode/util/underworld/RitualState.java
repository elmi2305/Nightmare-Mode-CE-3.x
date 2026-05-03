package com.itlesports.nightmaremode.util.underworld;

/**
 * State machine for the portal ritual.
 *
 *  INVALID     → structure is missing or broken
 *  VALID_IDLE  → structure intact, waiting for catalyst
 *  ACTIVE      → ritual running (beam + entity + storm)
 *  COMPLETE    → ritual finished, portal open
 *  FAILED      → ritual interrupted (structure broken mid-ritual)
 *
 * Transitions:
 *   INVALID      → VALID_IDLE   (structure validated on placement / periodic check)
 *   VALID_IDLE   → INVALID      (structure block removed)
 *   VALID_IDLE   → ACTIVE       (catalyst inserted)
 *   ACTIVE       → FAILED       (structure broken during ritual)
 *   ACTIVE       → COMPLETE     (ritual duration elapsed)
 *   FAILED       → INVALID      (reset after penalty delay)
 *   COMPLETE     is terminal; core should be consumed or replaced externally
 */
public enum RitualState {
    INVALID,
    VALID_IDLE,
    ACTIVE,
    COMPLETE,
    FAILED
}