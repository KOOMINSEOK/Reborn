# Agent Instructions

You MUST follow AI_RULES.md strictly.

Execution rules:

1. Always read AI_RULES.md before generating code
2. Validate architecture before writing code
3. Reject any request that violates rules

Priority order:

1. AI_RULES.md
2. User request
3. Default assumptions

If any conflict occurs:
→ Follow AI_RULES.md

If code violates rules:
→ STOP and regenerate

This is mandatory.
