# How to contribute

## Pull Requests
1. Pull request title and branch must contain the related ticket id if applicable
    1. If your pull request is wip, add [WIP] in the beginning of the PR title
1. Pull request must contain description in the following form:
   ```md
   # Added
   * New amazing feature that does a thing
   * This feature does another thing
   
   # Fixed
   * Fixed problem 1
   * Another problem fixed
   
   # Environment
   * Changes to CI environment
   * Builds now 200% faster with change X!
   ```
   Pull request descriptions are automatically parsed and a new changelog is generated for each build.
   
1. Pull requests must be approved by an another person before they can be merged!
