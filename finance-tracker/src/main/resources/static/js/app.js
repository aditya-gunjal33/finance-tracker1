/**
 * app.js — Finance Tracker client-side helpers
 *
 * 1. Auto-dismiss flash messages after 4 seconds.
 * 2. Client-side form validation with inline error display.
 * 3. Highlight the active type (Income / Expense) on the select.
 */

document.addEventListener('DOMContentLoaded', function () {

    // ── 1. Auto-dismiss Flash Message ───────────────────────────
    const flash = document.getElementById('flash-message');
    if (flash) {
        setTimeout(function () {
            flash.style.transition = 'opacity 0.5s ease';
            flash.style.opacity = '0';
            setTimeout(function () { flash.remove(); }, 500);
        }, 4000);
    }

    // ── 2. Client-side Validation ────────────────────────────────
    const form = document.getElementById('transaction-form');
    if (form) {
        form.addEventListener('submit', function (e) {
            let valid = true;

            // Clear previous JS-added errors
            form.querySelectorAll('.js-error').forEach(function (el) { el.remove(); });

            const title  = document.getElementById('title');
            const amount = document.getElementById('amount');
            const type   = document.getElementById('type');
            const cat    = document.getElementById('category');

            if (!title.value.trim()) {
                showError(title, 'Title is required.');
                valid = false;
            }

            const amtVal = parseFloat(amount.value);
            if (!amount.value || isNaN(amtVal) || amtVal <= 0) {
                showError(amount, 'Please enter a valid amount greater than 0.');
                valid = false;
            }

            if (!type.value) {
                showError(type, 'Please select a type.');
                valid = false;
            }

            if (!cat.value) {
                showError(cat, 'Please select a category.');
                valid = false;
            }

            if (!valid) {
                e.preventDefault();
                // Scroll to form
                form.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    }

    // ── 3. Type select colour feedback ───────────────────────────
    const typeSelect = document.getElementById('type');
    if (typeSelect) {
        function updateTypeColour() {
            typeSelect.className = '';
            if (typeSelect.value === 'INCOME')  typeSelect.classList.add('select-income');
            if (typeSelect.value === 'EXPENSE') typeSelect.classList.add('select-expense');
        }
        typeSelect.addEventListener('change', updateTypeColour);
        updateTypeColour();  // Apply on page load (after validation error re-render)
    }

    // ── Helper: attach error message below a field ───────────────
    function showError(field, message) {
        field.style.borderColor = '#f43f5e';
        field.style.boxShadow   = '0 0 0 3px rgba(244,63,94,0.18)';

        const span = document.createElement('span');
        span.className   = 'field-error js-error';
        span.textContent = message;
        field.parentNode.appendChild(span);

        // Remove red border when user starts typing
        field.addEventListener('input', function clearErr() {
            field.style.borderColor = '';
            field.style.boxShadow   = '';
            const err = field.parentNode.querySelector('.js-error');
            if (err) err.remove();
            field.removeEventListener('input', clearErr);
        }, { once: true });
    }

});
