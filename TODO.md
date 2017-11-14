# Documentation of technical debt

This file contains things that are considered technical debt and should be fixed in the medium term.
Do not add functional requirements here.

## OTE

### Upgrading MaterialUI to 1.0

Currently (Oct 2017) material UI is at version 0.19.x and the 1.0 branch is in beta.
The 0.19.x branch contains some bugs that need to be worked around.

Upgrade to 1.0 branch when it is released and remove any ugly workarounds.

* `ote.views.place-search/monkey-patch-chip-backspace`

### Responsive header
Currently listening window resize event. Change to css media query.

### Refactor OTE - front page
Currently we do not have a front page in OTE. When we are sure that
it is not needed, remove all front-page instances away from OTE.

## CKAN

* Monkey patched default CKAN authentication in napote_theme plugin.py to support login with email, because no simple way 
for overriding or extending the default functionality was provided.
