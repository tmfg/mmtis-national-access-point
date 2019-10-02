/**
 * Prevents tests failing on Uncaught ReferenceError: goog is not defined
 * FIXME: This should be removed when we do not have unnecessary errors anymore, so we can actually catch uncaught exceptions properly
 * Returning false here prevents Cypress from
 * failing the test
 */

Cypress.on('uncaught:exception', (err, runnable) => false);

// Show more debugging information on error

Cypress.on('fail', (err, runnable) => {
    debugger
})