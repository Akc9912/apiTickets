---
name: tech-adoption-planning
description: "Use when: evaluating and planning adoption of any new technology (cache, messaging, search, auth, observability, etc.) with phased rollout, risks, and prioritized tasks; documentation only"
---

# Tech Adoption Planning Skill

## Goal

Evaluate and plan adoption of a new technology with a safe, phased implementation backlog.

## Inputs

- Technology to adopt (any).
- Target scope (file/module/system).
- Desired outcomes (latency, security, scalability, cost, DX).
- Constraints (team capacity, infra, timeline, compatibility).

## Workflow

1. Baseline current state:
   - Existing architecture and bottlenecks.
   - Security and operational constraints.
2. Fit assessment:
   - Why this technology, where it fits, where it does not.
   - Alternatives and tradeoffs.
3. Integration design:
   - Required changes in services/controllers/repositories/config.
   - Data flow, failure modes, fallback behavior.
4. Rollout strategy:
   - Phase 1: foundation
   - Phase 2: pilot in one module
   - Phase 3: broader rollout
   - Phase 4: hardening and observability
5. Backlog generation in TODO format:
   - `// P0 SECURITY [module] ...`
   - `// P1 REFACTOR [module] ...`
   - `// P2 TECHDEBT [module] ...`
   - `// P3 DOCS [module] ...`
6. Ticket recommendations:
   - Always for P0/P1 and cross-module tasks.

## Output Format

- Decision summary (adopt / not now / reject with rationale).
- Phased implementation plan.
- Prioritized tasks by module.
- Risks, rollback, and validation checklist.

## Guardrails

- Prefer incremental rollout over big-bang migration.
- Do not introduce secrets in source code.
- Keep auth/identity aligned with Supabase when relevant.
- Keep TODO comments one line and actionable.
- Documentation only: output plans/backlogs/tasks without implementing code changes.
