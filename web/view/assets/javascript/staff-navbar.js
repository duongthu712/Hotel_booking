document.addEventListener("DOMContentLoaded", function () {
    const currentUrl = window.location.href;
const navLinks = document.querySelectorAll('.staff-navbar a');

    navLinks.forEach(link => {
        const linkUrl = link.href;

        if (currentUrl === linkUrl || currentUrl.includes(linkUrl)) {
            link.classList.add('active');
        }
    });
});