# .agents Directory

This directory contains planning documents and guides for AI agents and developers working on the deploy4j project.

## Purpose

The `.agents` directory serves as a knowledge base for:
- Project planning and strategy documents
- Task breakdowns and implementation checklists
- Developer guides and best practices
- Reference materials for future work

## Contents

### Test Planning Documents

1. **[unit-test-plan.md](./unit-test-plan.md)**
   - Comprehensive test strategy for deploy4j-core and deploy4j-ext modules
   - Detailed analysis of test coverage gaps (80 classes without tests)
   - Prioritized implementation phases (P0-P3)
   - Test complexity assessment
   - Estimated effort and resource allocation

2. **[test-implementation-checklist.md](./test-implementation-checklist.md)**
   - Quick-reference checklist for tracking test implementation progress
   - Organized by priority phase (P0-P3)
   - Progress tracking with checkboxes
   - Summary statistics and completion percentages

3. **[test-writing-guide.md](./test-writing-guide.md)**
   - Practical guide for writing tests in the deploy4j project
   - Code templates and examples
   - Mocking guidelines and best practices
   - Common testing patterns
   - AssertJ fluent assertions reference

## Usage

### For AI Agents

When working on test implementation tasks:
1. Refer to `unit-test-plan.md` to understand priorities and strategy
2. Use `test-implementation-checklist.md` to track progress
3. Follow patterns in `test-writing-guide.md` for implementation
4. Update the checklist as tests are completed

### For Developers

1. **Starting Test Implementation:**
   - Review the test plan to understand the overall strategy
   - Check the checklist to see what needs testing
   - Follow the writing guide for coding standards

2. **Tracking Progress:**
   - Update the checklist as you complete tests
   - Mark items as in-progress (🔄) or completed (✅)
   - Update the progress statistics

3. **Writing New Tests:**
   - Use the templates in the writing guide
   - Follow the naming conventions
   - Apply the testing patterns shown in examples

## Document Maintenance

### When to Update

- **After completing test implementation:** Update the checklist
- **When priorities change:** Revise the test plan
- **When discovering new patterns:** Add to the writing guide
- **After code reviews:** Document lessons learned

### Version History

| Date | Version | Author | Description |
|------|---------|--------|-------------|
| 2025-10-23 | 1.0 | AI Agent | Initial test plan creation |

## Related Resources

- Main project [README.md](../README.md)
- [pom.xml](../pom.xml) - Maven configuration
- Existing tests in `deploy4j-core/src/test/java/`
- Existing tests in `deploy4j-ext/src/test/java/`

## Statistics

**Current Coverage (as of 2025-10-23):**
- Total classes: 90
- Classes with tests: 10 (11%)
- Classes without tests: 80 (89%)

**Breakdown by Priority:**
- P0 (Critical): 8 classes
- P1 (High): 41 classes  
- P2 (Medium): 29 classes
- P3 (Low): 3 classes

## Contributing

When adding new documents to this directory:
1. Follow the markdown formatting style
2. Update this README with a description
3. Keep documents focused and actionable
4. Include examples where helpful

---

*This directory is maintained as part of the deploy4j project to support both human developers and AI agents in understanding and implementing project tasks.*
