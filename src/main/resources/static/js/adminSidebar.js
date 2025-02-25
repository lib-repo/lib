document.addEventListener("DOMContentLoaded", function () {
      const sidebar = document.getElementById("adminSidebar");
      const toggleButton = document.getElementById("sidebarToggle");

      toggleButton.addEventListener("click", function (event) {
        event.preventDefault();
        if (sidebar.style.display === "none" || sidebar.style.display === "") {
          sidebar.style.display = "block";
        } else {
          sidebar.style.display = "none";
        }
      });
    });