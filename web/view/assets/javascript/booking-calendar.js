function setupCalendarLogic(checkInId, checkOutId) {
    const checkInInput = document.getElementById(checkInId);
    const checkOutInput = document.getElementById(checkOutId);

    if (!checkInInput || !checkOutInput) return;

    let checkOutPicker;

    const checkInPicker = flatpickr(checkInInput, {
        dateFormat: "d/m/Y",
        minDate: "today",

        onChange: function (selectedDates) {
            if (selectedDates.length === 0) return;

            const nextDay = new Date(selectedDates[0]);
            nextDay.setDate(nextDay.getDate() + 1);

            checkOutPicker.set("minDate", nextDay);

            const currentOut = checkOutPicker.selectedDates[0];

            if (!currentOut || currentOut <= selectedDates[0]) {
                checkOutPicker.setDate(nextDay);
            }
        }
    });

    checkOutPicker = flatpickr(checkOutInput, {
        dateFormat: "d/m/Y",
        minDate: new Date().fp_incr(1)
    });
}

function initCalendar() {
    setupCalendarLogic('checkIn', 'checkOut');
    setupCalendarLogic('checkInResult', 'checkOutResult');
}

document.addEventListener('DOMContentLoaded', initCalendar);