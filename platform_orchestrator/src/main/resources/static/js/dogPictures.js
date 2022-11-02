window.onload = function () {
    getUserDoc();
};

var actNumber;
var contractNumber;
var contractDate;
var fullName;
var passportSeries;
var passportNumber;
var passportIssued;
var issueDate;
var birthDate;
var registration;
var email;
var phoneNumber;
let telegramNotification;
let vkNotification;
let currentUserLogin;
let telegramCheck;
let vkCheck;


function getUserDoc() {
    $.ajax({
        method: 'GET',
        url: '/dogPictures/currentUser',
        contentType: 'application/json',
        success: function (response) {
            console.log(response);
            drawColumns(response);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function drawColumns(data) {
    while (document.getElementById("userDoc").getElementsByTagName("tbody")[0].rows[0])
        document.getElementById("userDoc").getElementsByTagName("tbody")[0].deleteRow(0);
    for (let i = 0; i < data.length; i++) {
        addColumn(data[i]);
    }
}

function addColumn(data) {
    let table = document.getElementById("userDoc").getElementsByTagName("tbody")[0];
    let tr = table.insertRow(table.rows.length);
    let td;

    insertTd(data.name, tr);
    insertTd(data.type, tr);

    let downloadBtn = document.createElement("button");
    downloadBtn.className = "download btn-success";
    downloadBtn.innerHTML = "Download";
    downloadBtn.type = "submit";
    downloadBtn.style = "float: left";
    downloadBtn.addEventListener("click", () => {
        downloadFile(data.id, data.name);
    });
    td = tr.insertCell(2);
    td.insertAdjacentElement("beforeend", downloadBtn);
}

function insertTd(value, parent) {
    let element = document.createElement("td");
    element.scope = "row";
    element.innerText = value;
    parent.insertAdjacentElement("beforeend", element);
}

function downloadFile(id, name) {
    $.ajax({
        url: '/document/download/' + id,
        dataType: 'binary',
        xhrFields: {
            'responseType': 'blob'
        },
        success: function (data, status, xhr) {
            var blob = new Blob([data], {type: xhr.getResponseHeader('Content-Type')});
            var link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = '' + name;
            link.click();
        }
    });
}
