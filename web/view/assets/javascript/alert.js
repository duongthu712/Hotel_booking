document.addEventListener("DOMContentLoaded", function () {

    const alerts = document.querySelectorAll(".alert-message");
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = "0";
            alert.style.transform = "translateY(-10px)";
            alert.style.transition = "opacity 0.3s ease, transform 0.3s ease";
            setTimeout(() => { alert.remove(); }, 300);
        }, 3000);
    });
});