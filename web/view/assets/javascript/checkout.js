document.addEventListener("DOMContentLoaded", function () {
    const actualTimeInput = document.getElementById("actual-checkout-time");
    const displayDateTimeSpan = document.getElementById("display-time");
    const hiddenCheckoutTimeInput = document.getElementById("hidden-checkout-time");
    const checkoutLinks = document.querySelectorAll(".btn-checkout");

    function pad2(n) { return n < 10 ? "0" + n : "" + n; }

    //ISO yyyy-MM-ddTHH:mm
    function toIsoLocalDateTime(date) {
        return date.getFullYear() + "-" +
            pad2(date.getMonth() + 1) + "-" +
            pad2(date.getDate()) + "T" +
            pad2(date.getHours()) + ":" +
            pad2(date.getMinutes());
    }

    //nhấn nút CHECK-OUT ở danh sách
    checkoutLinks.forEach(function (link) {
        link.addEventListener("click", function () {
            const now = new Date();
            const separator = link.href.indexOf("?") > -1 ? "&" : "?";
            link.href += separator + "actualCheckoutTime=" + encodeURIComponent(toIsoLocalDateTime(now));
        });
    });

    //giao diện chi tiết
    if (actualTimeInput) {
        const params = new URLSearchParams(window.location.search);
        const isoCheckoutTime = params.get("actualCheckoutTime") || toIsoLocalDateTime(new Date());

        const datePart = isoCheckoutTime.split("T")[0];
        const timePart = isoCheckoutTime.split("T")[1];
        
        const [y, m, d] = datePart.split("-");
        const formatted24h = d + "/" + m + "/" + y + " " + timePart;

        actualTimeInput.value = formatted24h;
        
        if (displayDateTimeSpan) {
            displayDateTimeSpan.textContent = formatted24h;
        }
        if (hiddenCheckoutTimeInput) {
            hiddenCheckoutTimeInput.value = isoCheckoutTime;
        }
    }
});