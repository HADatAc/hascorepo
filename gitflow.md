Our version control policy follows a structured flow that guarantees organization and stability in software development. For reference, our approach shares similarities with the Git Flow, which is widely used to organize projects with multiple releases and hotfixes.

More details about Git Flow can be found here: https://ionixjunior.dev/en/git-branching-strategies-a-comprehensive-guide/

Here are the main and specific points of our process:

## Main Branch

We have the main branch, which always contains a stable version ready for release. This branch reflects the production state, ensuring the code is safe and free from critical bugs.

## Development Branches:

Below the main branch, we use a development branch called DEVELOPMENT_V0.x, where we integrate new features or planned improvements for the next version. As we work on functionalities, we create level 3 branches with names that describe the specific change or task. These branches are associated with issues, ensuring clear tracking of activities.

## Release Cycle:

When all the objectives of the version (for example, V0.7) are met, we merge the DEVELOPMENT_V0.7 branch into main and rename this branch to RELEASE_V0.7. This ensures we maintain a clear and traceable version of each release. Next, we create a new development branch, like DEVELOPMENT_V0.8, and continue working on the next version.

## Hotfixes:

To address critical issues that arise in production, we create hotfix branches from main. Once the problem is resolved, we merge the hotfix into both main and the active development branch (DEVELOPMENT_V0.x), ensuring that the fix is available in all subsequent versions.

## Issues and Pull Requests:

Each change is documented through issues, which are linked to level 3 branches. When a task is completed, we create a Pull Request (PR) for review, and once approved, the level 3 branch is deleted after merging into the development branch.
This process organizes our development and facilitates tracking changes, ensuring all versions are stable and urgent fixes are applied promptly.