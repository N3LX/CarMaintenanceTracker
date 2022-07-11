/**
 * This script is responsible for fetching and displaying the data in the main part of the page
 */
//Variable to track the currently selected user for user across the onClick functions
var currentUserId;
var currentUserName;
var currentElement;

displayInitialState();

function displayInitialState() {
    document.querySelector(".main-content").innerHTML = "<p>Create a new user using <i class=\"bi bi-file-plus\"></i> or access exisiting ones from the sidebar.<p>";
}

function displayUser(element) {
    //Get user id and name from html element
    var userId = element.id.substring(4);
    var userName = element.innerHTML;

    //Store them in global variables for later use
    currentUserId = userId;
    currentUserName = userName;
    currentElement = element;

    //Create HTML content
    //First create the header with user's name
    var content = "<h1>" + userName + "</h1>";
    content += "<a>Add new vehicle <i class=\"bi bi-file-plus\" onClick=\"addVehicle()\"></i></a><br/><br/>";

    fetch(api_url + "vehicles")
        .then(function (apiResponse) {
            return apiResponse.json();
        })
        .then(async function (json) {
            //Sort json objects by id
            json.sort(function (a, b) {
                return a.id - b.id;
            })
            //Filter vehicles belonging to user
            for (var i = 0; i < json.length; i++) {
                var vehicle = json[i];
                if (vehicle.ownerId == userId) {
                    content += await getVehicleAsHtml(vehicle);
                }
            }
            //Display the content
            document.querySelector(".main-content").innerHTML = content;
            enableCollapsibleElements();
        })
}

async function getVehicleAsHtml(vehicle) {
    var vehicleData = '<button type="button" class="collapsible">'
        + vehicle.customName
        + '<span><i class="bi bi-pencil"  onClick="editVehicle(' + vehicle.id + ')"></i>'
        + " <i class=\"bi bi-file-minus\" onClick=\"deleteVehicle(" + vehicle.id + ")\"></i>"
        + '</span></button>'
        + '<div class="collapsible-content">'
        + '<p>' + vehicle.make + ' ' + vehicle.model + '</p>'
        + '<a>Add new record <i class="bi bi-file-plus" onClick="addRecord(' + vehicle.id + ')"></i></a>';

    var records = await getRecordsAsHtml(vehicle.id);

    var vehicleDataClosingTag = "</div>"

    return vehicleData + records + vehicleDataClosingTag;
}

async function getRecordsAsHtml(vehicleId) {
    //Get records
    var response = await fetch(api_url + "records");
    var json = await response.json();

    //Sort json objects by id
    json.sort(function (a, b) {
        return a.id - b.id;
    })

    var content = "";
    //Filter records belonging to vehicle
    for (var i = 0; i < json.length; i++) {
        var record = json[i];
        if (record.vehicleId == vehicleId) {
            var recordAsHtml = '<div class="record"><p class="record-title">Record<span>'
                + '<i class="bi bi-pencil" onClick="editRecord(' + record.id + ',' + vehicleId + ')"></i>'
                + '<i class="bi bi-file-minus" onClick="deleteRecord(' + record.id + ')"></i>'
                + '</span></p>'
                + 'Creation date: ' + record.creationDate[2] + '-' + record.creationDate[1] + '-' + record.creationDate[0] + '<br/>'
                + 'Mileage: ' + record.mileage + '<br/>'
                + 'Short description: ' + record.shortDescription + '<br/>'
                + 'Long description: ' + record.longDescription
                + '</div>'

            content += recordAsHtml;
        }
    }
    return content;
}

function editVehicle(id) {
    //Prompt client for vehicle data
    var customName = null
    while (customName == null || customName == "") {
        customName = prompt("Enter new custom name for vehicle:");
        if (customName == "") {
            alert("Custom name cannot be empty.");
        }
        if (customName == null) {
            return;
        }
    }

    var make = null
    while (make == null || make == "") {
        make = prompt("Enter new make of vehicle:");
        if (make == "") {
            alert("Make cannot be empty.");
        }
        if (make == null) {
            return;
        }
    }

    var model = null
    while (model == null || model == "") {
        model = prompt("Enter new model of vehicle:");
        if (model == "") {
            alert("Model cannot be empty.");
        }
        if (model == null) {
            return;
        }
    }

    if (customName == null || make == null || model == null) {
        return;
    }

    //Prepare JSON payload
    var requestBody = '{"id": ' + id + ',\n'
        + '"customName": "' + customName + '",'
        + '"ownerId": ' + currentUserId + ','
        + '"make": "' + make + '",'
        + '"model": "' + model + '"'
        + '}';

    //Prepare PUT request and send it
    fetch(api_url + "vehicles", {
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
                var errorMessage = "Could not process your request.\n" + json.message;
                alert(errorMessage);
            }
        }).then(function () {
            displayUser(currentElement)
        })
}

function addVehicle() {
    //Prompt client for vehicle data
    var customName = null
    while (customName == null || customName == "") {
        customName = prompt("Enter custom name for new vehicle:");
        if (customName == "") {
            alert("Custom name cannot be empty.");
        }
        if (customName == null) {
            return;
        }
    }

    var make = null
    while (make == null || make == "") {
        make = prompt("Enter make of new vehicle:");
        if (make == "") {
            alert("Make cannot be empty.");
        }
        if (make == null) {
            return;
        }
    }

    var model = null
    while (model == null || model == "") {
        model = prompt("Enter model of new vehicle:");
        if (model == "") {
            alert("Model cannot be empty.");
        }
        if (model == null) {
            return;
        }
    }

    if (customName == null || make == null || model == null) {
        return;
    }

    //Prepare JSON payload
    var requestBody = '{"id": ' + 0 + ',\n'
        + '"customName": "' + customName + '",'
        + '"ownerId": ' + currentUserId + ','
        + '"make": "' + make + '",'
        + '"model": "' + model + '"'
        + '}';

    //Prepare POST request and send it
    fetch(api_url + "vehicles", {
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
                var errorMessage = "Could not process your request.\n" + json.message;
                alert(errorMessage);
            }
        }).then(function () {
            displayUser(currentElement)
        })
}

function addRecord(vehicleId) {
    //Prompt client for record data
    var mileage = null
    while (mileage == null || mileage == "") {
        mileage = prompt("Enter vehicle's current mileage:");
        if (mileage == "") {
            alert("Mileage cannot be empty.");
        }
        if (mileage == null) {
            return;
        }
    }

    var shortDescription = null
    while (shortDescription == null || shortDescription == "") {
        shortDescription = prompt("Enter short description:");
        if (shortDescription == "") {
            alert("Description cannot be empty.");
        }
        if (shortDescription == null) {
            return;
        }
    }

    var longDescription = null
    while (longDescription == null || longDescription == "") {
        longDescription = prompt("Enter long description:");
        if (longDescription == "") {
            alert("Description cannot be empty.");
        }
        if (longDescription == null) {
            return;
        }
    }

    if (mileage == null || shortDescription == null || longDescription == null) {
        return;
    }

    let today = new Date();
    var dd = String(today.getDate()).padStart(2, '0');
    var mm = String(today.getMonth() + 1).padStart(2, '0');
    var yyyy = today.getFullYear();
    var creationDate = yyyy + '-' + mm + '-' + dd;

    //Prepare JSON payload
    var requestBody = '{"id": ' + 0 + ',\n'
        + '"vehicleId": ' + vehicleId + ','
        + '"creationDate": "' + creationDate + '",'
        + '"mileage": ' + mileage + ','
        + '"shortDescription": "' + shortDescription + '",'
        + '"longDescription": "' + longDescription + '"'
        + '}';

    //Prepare POST request and send it
    fetch(api_url + "records", {
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
                var errorMessage = "Could not process your request.\n" + json.message;
                alert(errorMessage);
            }
        }).then(function () {
            displayUser(currentElement)
        })
}

function editRecord(recordId, vehicleId) {
    //Prompt client for record data
    var mileage = null
    while (mileage == null || mileage == "") {
        mileage = prompt("Enter new mileage:");
        if (mileage == "") {
            alert("Mileage cannot be empty.");
        }
        if (mileage == null) {
            return;
        }
    }

    var shortDescription = null
    while (shortDescription == null || shortDescription == "") {
        shortDescription = prompt("Enter new short description:");
        if (shortDescription == "") {
            alert("Description cannot be empty.");
        }
        if (shortDescription == null) {
            return;
        }
    }

    var longDescription = null
    while (longDescription == null || longDescription == "") {
        longDescription = prompt("Enter new long description:");
        if (longDescription == "") {
            alert("Description cannot be empty.");
        }
        if (longDescription == null) {
            return;
        }
    }

    if (mileage == null || shortDescription == null || longDescription == null) {
        return;
    }

    let today = new Date();
    var dd = String(today.getDate()).padStart(2, '0');
    var mm = String(today.getMonth() + 1).padStart(2, '0');
    var yyyy = today.getFullYear();
    var creationDate = yyyy + '-' + mm + '-' + dd;

    //Prepare JSON payload
    var requestBody = '{"id": ' + recordId + ',\n'
        + '"vehicleId": ' + vehicleId + ','
        + '"creationDate": "' + creationDate + '",'
        + '"mileage": ' + mileage + ','
        + '"shortDescription": "' + shortDescription + '",'
        + '"longDescription": "' + longDescription + '"'
        + '}';

    //Prepare PUT request and send it
    fetch(api_url + "records", {
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
                var errorMessage = "Could not process your request.\n" + json.message;
                alert(errorMessage);
            }
        }).then(function () {
            displayUser(currentElement)
        })
}

async function deleteRecord(recordId) {
    //Get full record entry
    await fetch(api_url + "records/" + recordId, {
        method: "get",
        headers: new Headers({
            'Content-Type': 'application/json'
        })
    })
        .then(function (response) {
            return response.json();
        })
        .then(async function (record) {
            //Request to delete this record
            await fetch(api_url + "records", {
                method: "delete",
                body: JSON.stringify(record),
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
                        var errorMessage = "Could not process your request.\n" + json.message;
                        alert(errorMessage);
                    }
                }).then(function () {
                    displayUser(currentElement)
                })
        })
}

async function deleteVehicle(vehicleId) {
    await fetch(api_url + "records")
        .then(function (apiResponse) {
            return apiResponse.json();
        })
        .then(async function (json) {
            //Filter records belonging to vehicle and delete them
            for (var i = 0; i < json.length; i++) {
                var record = json[i];
                if (record.vehicleId == vehicleId) { }
                await deleteRecord(record.id);
            }
        })
        .then(async function () {
            //Get full vehicle entry as a JSON object from API
            await fetch(api_url + "vehicles/" + vehicleId, {
                method: "get",
                headers: new Headers({
                    'Content-Type': 'application/json'
                })
            })
                .then(function (response) {
                    return response.json();
                })
                .then(async function (vehicle) {
                    //Request to delete this vehicle
                    await fetch(api_url + "vehicles", {
                        method: "delete",
                        body: JSON.stringify(vehicle),
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
                            displayUser(currentElement);
                        })
                })
        })
}