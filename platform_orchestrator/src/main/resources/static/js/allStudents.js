getStudents();
btnClickListener();

function getStudents() {
    $.ajax({
        url: '/platformUsers',
        type: 'GET',
        contentType: 'application/json',
        success: function (response) {
            drawColumns(response);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function drawColumns(data) {
    while (document.getElementById("requests-table").getElementsByTagName("tbody")[0].rows[0])
        document.getElementById("requests-table").getElementsByTagName("tbody")[0].deleteRow(0);
    for (let i = 0; i < data.length; i++) {
        if (data[i].studyStatus === "ACTIVE" && data[i].authorities[0].authority !== "ROLE_GRADUATE") {
            addColumn(data[i]);
        }
    }
    for (let i = 0; i < data.length; i++) {
        if (data[i].authorities[0].authority === "ROLE_GRADUATE" && data[i].studyStatus !== "BAN") {
            addColumn(data[i]);
        }
    }
    for (let i = 0; i < data.length; i++) {
        if (data[i].studyStatus === "BAN") {
            addColumn(data[i]);
        }
    }
}

function addColumn(data) {
    let table = document.getElementById("requests-table").getElementsByTagName("tbody")[0];
    let tr = table.insertRow(table.rows.length);
    let td;

    let color;
    color = 'orange';
    let legend;
    legend = "CORE";

    if (data.coursePart === "WEB") {
        color = 'blue';
        legend = "WEB";
    }

    if (data.coursePart === "PREPROJECT") {
        color = 'green';
        legend = "PREPROJECT";
    }

    insertTd(data.id, tr);
    insertTd(data.login, tr);

    let updateBtn = document.createElement("button");
    updateBtn.className = "btn btn-success";
    updateBtn.innerHTML = "Повысить";
    updateBtn.type = "submit";

    let courseBtn = document.createElement("button");
    courseBtn.className = "btn btn-success";
    courseBtn.innerHTML = legend;
    courseBtn.title = legend;
    courseBtn.style.backgroundColor = color;
    courseBtn.type = "submit";

    if (data.authorities[0].authority === "ROLE_GRADUATE") {
        document.getElementsByClassName("finish-education").disable = true;
    }
    if (data.studyStatus === "BAN") {
        updateBtn.disabled = true;
    }
    updateBtn.addEventListener("click", () => {
        updateUserRole(data.id, "ADMIN");
    });
    td = tr.insertCell(2);
    td.insertAdjacentElement("beforeend", courseBtn);
    td = tr.insertCell(3);
    td.insertAdjacentElement("beforeend", updateBtn);
    td = tr.insertCell(4);

    if (data.studyStatus === "ACTIVE" && data.authorities[0].authority === "ROLE_USER") {

        courseBtn.addEventListener("click", () => {
            if (data.coursePart === "CORE") {
                updateCurrentCoursePart(data.id, "WEB");
            } else {
                updateCurrentCoursePart(data.id, "PREPROJECT");
            }
        });

        td.insertAdjacentHTML("beforeend",
            `
            <button type="button" class="btn btn-primary finish-education" data-toggle="modal" data-target="#aceptModal${data.id}">
                Завершить обучение
            </button>

            <div class="modal fade" id="aceptModal${data.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
              <div class="modal-dialog" role="document">
                <div class="modal-content">
                  <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">Устроился на работу?</h5>
                    <button type="button" class="btn-close" data-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal" data-btn="working" data-id = "${data.id}">Устроился</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" data-btn="ban" data-id = "${data.id}" >Забанить</button>
                  </div>
                </div>
              </div>
            </div>            
           `
        )
    }
    if (data.authorities[0].authority === "ROLE_GRADUATE") {
        let status = document.createElement("span");
        status.innerHTML = "GRADUATE ";
        td.insertAdjacentElement("beforeend", status);
    }
    if (data.studyStatus === "BAN") {
        let status = document.createElement("span");
        status.innerHTML = data.studyStatus;
        td.insertAdjacentElement("beforeend", status);
    }
    ;
    td = tr.insertCell(4);
    let codeTries = document.createElement("button");
    codeTries.className = "btn btn-success";
    codeTries.innerHTML = "Посмотреть решения";

    td.insertAdjacentElement("beforeend", codeTries);

    codeTries.addEventListener("click", () => {
        sessionStorage.setItem("admin", "yes")
        sessionStorage.setItem("id", data.id)
        window.location.href = "/codeTryList";
    });


}

function setWorkStatus(id, status) {
    let data = {};
    data.id = id;
    $.ajax({
        url: '/platformUsers/' + id + '/' + status,
        type: 'PUT',
        contentType: 'application/json',
        success: function () {
            getStudents();
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function btnClickListener() {
    let table = document.getElementById("requests-table").getElementsByTagName("tbody")[0];
    table.addEventListener("click", event => {
        const working = event.target.dataset.btn === "working";
        const ban = event.target.dataset.btn === "ban";
        if (working) {
            updateUserRole(event.target.dataset.id, "GRADUATE");
            getStudents();
        }
        if (ban) {
            setWorkStatus(event.target.dataset.id, "BAN");
            getStudents();
        }
    });
}

function updateUserRole(id, status) {
    let confirmation = confirm("Вы уверены, что хотите повысить студента до " + status + " ?");
    if (confirmation === true) {
        let data = {};
        data.id = id;
        $.ajax({
            url: '/platformUsers/' + id + '/' + status,
            type: 'POST',
            contentType: 'application/json',
            success: function () {
                getStudents();
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}

function updateCurrentCoursePart(id, coursePart) {
    let confirmation = confirm("Вы уверены, что хотите перевести пользователя на этап " + coursePart + " ?");
    if (confirmation === true) {
        let data = {};
        data.id = id;
        $.ajax({
            url: '/platformUsers/promoteCoursePart/' + id + '/' + coursePart,
            type: 'POST',
            contentType: 'application/json',
            success: function () {
                getStudents();
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}

function insertTd(value, parent) {
    let element = document.createElement("td");
    element.scope = "row";
    element.innerText = value;
    parent.insertAdjacentElement("beforeend", element)
}
