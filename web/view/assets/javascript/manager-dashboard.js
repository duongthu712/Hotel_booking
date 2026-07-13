document.addEventListener("DOMContentLoaded", function () {
    initRevenueChart();
});

function initRevenueChart() {
    const ctx = document.getElementById("revenueChart");
    const chartContainer = document.querySelector(".chart-container");

    if (!ctx || !chartContainer) {
        console.warn("Không tìm thấy canvas hoặc container biểu đồ");
        return;
    }

    const labelsAttr = chartContainer.getAttribute("data-labels");
    const revenuesAttr = chartContainer.getAttribute("data-revenues");

    if (!labelsAttr || !revenuesAttr) {
        console.warn("Không tìm thấy dữ liệu biểu đồ");
        return;
    }

    let labels, revenues;
    try {
        // Chuẩn hoá về double quote trước khi parse
        labels = JSON.parse(labelsAttr.replace(/'/g, '"'));
        revenues = JSON.parse(revenuesAttr.replace(/'/g, '"'));
    } catch (e) {
        console.error("Lỗi parse dữ liệu biểu đồ:", e);
        return;
    }

    if (!Array.isArray(labels) || !Array.isArray(revenues)) {
        console.warn("Dữ liệu biểu đồ không hợp lệ");
        return;
    }

    new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                    label: "Doanh thu (VND)",
                    data: revenues,
                    backgroundColor: "rgba(36, 71, 105, 0.8)",
                    borderColor: "rgba(36, 71, 105, 1)",
                    borderWidth: 1,
                    borderRadius: 4,
                    hoverBackgroundColor: "rgba(212, 175, 55, 0.9)"
                }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: "rgba(36, 71, 105, 0.95)",
                    titleFont: {
                        family: "'Cormorant Garamond', serif",
                        size: 14
                    },
                    bodyFont: {
                        family: "'Cormorant Garamond', serif",
                        size: 13
                    },
                    padding: 12,
                    cornerRadius: 6,
                    callbacks: {
                        label: function (context) {
                            return "Doanh thu: " + context.parsed.y.toLocaleString("vi-VN") + " VND";
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {display: false},
                    ticks: {
                        font: {family: "'Cormorant Garamond', serif", size: 12},
                        color: "#8e8a82"
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: "rgba(27, 27, 27, 0.05)",
                        drawBorder: false
                    },
                    ticks: {
                        font: {family: "'Cormorant Garamond', serif", size: 12},
                        color: "#8e8a82",
                        callback: function (value) {
                            if (value >= 1000000)
                                return (value / 1000000).toFixed(1) + "M";
                            if (value >= 1000)
                                return (value / 1000).toFixed(0) + "K";
                            return value;
                        }
                    }
                }
            },
            animation: {
                duration: 1000,
                easing: "easeOutQuart"
            }
        }
    });
}