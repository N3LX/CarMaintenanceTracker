/**
 * This script calls the API and shows the returned data in form of a list in the sidebar
 */
const api_url = document.location.origin + ":8008/cmt-api/";

//Get data from API and show it
reloadSidebar();

function reloadSidebar() {
    fetch(api_url + "users")
        .then(function (apiResponse) {
            return apiResponse.json();
        })
        .then(function (json) {
            //Header at the top of the sidebar
            var html = "<h1>Users <i class=\"bi bi-file-plus\" onClick=\"addUser()\"></i></h1>\n";
            //Individual elements
            for (var i = 0; i < json.length; i++) {
                var user = json[i];
                html += userToHTML(user);
            }
            //Insert generated HTML code into the sidebar div
            document.querySelector(".sidebar").innerHTML = html;
        });
}

//Helper function used in reloadSidebar() for parsing data from API
function userToHTML(user) {
    return "<span><a href=\"#\" id=user"
        + user.id
        + ">"
        + user.userName
        + "</a>"
        + " <i class=\"bi bi-pencil\" onClick=\"editUser(" + user.id + ")\"></i>"
        + " <i class=\"bi bi-file-minus\" onClick=\"deleteUser(" + user.id + ")\"></i>"
        + "</span>\n";
}

function editUser(id) {
    //Prompt client for new username
    newUserName = null
    while (newUserName == null || newUserName == "") {
        newUserName = prompt("Enter new username:");
        if (newUserName == "") {
            alert("Username cannot be empty.");
        }
        if (newUserName == null) {
            return;
        }
    }

    //Prepare JSON payload
    requestBody = '{"id": ' + id + ',\n'
        + '"userName": "' + newUserName + '"}';

    //Prepare PUT request and send it
    fetch(api_url + "users", {
        method: "put",
        body: requestBody,
        headers: new Headers({
            'Content-Type': 'application/json'
        })
    })
        .then(function (apiResponse) {
            //If the request did not return expected HTTP status check for an error message and display it
            if (apiResponse.status != 200) {
                return apiResponse.json();
            }
            return null;
        }).then(function (json) {
            if (json != null) {
                errorMessage = "Could not process your request.\n" + json.message;
                alert(errorMessage);
            }
        }).then(function () {
            reloadSidebar();
        })
}

function deleteUser(id) {
    currentUserName = document.getElementById("user" + id).innerHTML;

    //Confirm operation
    if (!confirm("Are you sure you want to delete " + currentUserName + " and all of their associated vehicles?")) {
        return;
    }

    /*
    * TODO:
    * Record deletion
    * Vehicle deletion
    */

    //Delete the user

    //Prepare JSON payload
    requestBody = '{"id": ' + id + ',\n'
        + '"userName": "' + currentUserName + '"}';

    fetch(api_url + "users", {
        method: "delete",
        body: requestBody,
        headers: new Headers({
            'Content-Type': 'application/json'
        })
    })
        .then(function (apiResponse) {
            //If the request did not return expected HTTP status check for an error message and display it
            if (apiResponse.status != 204) {
                return apiResponse.json();
            }
            return null;
        }).then(function (json) {
            if (json != null) {
                errorMessage = "Could not process your request.\n" + json.message;
                alert(errorMessage);
            }
        }).then(function () {
            reloadSidebar();
        })
}

function addUser() {
    //Prompt client for username
    userName = null
    while (userName == null || userName == "") {
        userName = prompt("Enter username for new user:");
        if (userName == "") {
            alert("Username cannot be empty.");
        }
        if (userName == null) {
            return;
        }
    }

    //Prepare JSON payload
    requestBody = '{"id": ' + 0 + ',\n'
        + '"userName": "' + userName + '"}';

    //Prepare POST request and send it
    fetch(api_url + "users", {
        method: "post",
        body: requestBody,
        headers: new Headers({
            'Content-Type': 'application/json'
        })
    })
        .then(function (apiResponse) {
            //If the request did not return expected HTTP status check for an error message and display it
            if (apiResponse.status != 201) {
                return apiResponse.json();
            }
            return null;
        }).then(function (json) {
            if (json != null) {
                errorMessage = "Could not process your request.\n" + json.message;
                alert(errorMessage);
            }
        }).then(function () {
            reloadSidebar();
        })
}