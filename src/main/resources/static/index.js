
var currentUser;

function createLoginForm() {
    var loginFormTemplate = document.getElementById("login-form-template").content;
    var copyHTML = document.importNode(loginFormTemplate, true);
    copyHTML.querySelector("#login-form").setAttribute("id", "login-form");
    document.getElementById("login-form-root").appendChild(copyHTML);
}

function deleteLoginForm() {
    var node= document.getElementById("login-form-root");
    while (node.firstChild) {
        node.removeChild(node.firstChild);
    }
}

function login() {
    var user = new Object();
    user.userName = document.getElementById("inputLogin").value;
    user.password = document.getElementById("inputPassword").value;

    var xhrAddUser = new XMLHttpRequest();

    xhrAddUser.onreadystatechange = function() {
        if (xhrAddUser.readyState == 4 && xhrAddUser.status == 200) {
            currentUser = user;
            var userResponse = JSON.parse(xhrAddUser.responseText);
            deleteLoginForm();
            createNotesForm(userResponse);
        }
        else if (xhrAddUser.readyState == 4) {
            if(xhrAddUser.status == 403){
                alert('Password is not correct');
            }else if(xhrAddUser.status == 400){
                alert('Username or password is not valid');
            }

        }
    };
    var path = "/login";
    xhrAddUser.open('post', path);
    xhrAddUser.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhrAddUser.send(JSON.stringify(user));
}

function createNotesForm(user) {
    var notesFormTemplate = document.getElementById("notes-form-template").content;
    var copyHTML = document.importNode(notesFormTemplate, true);
    copyHTML.querySelector("#userNameText").innerHTML = "HELLO " + user.userName;
    for (i=0; i<user.notesList.length;i++){
        createNoteWidget(user.notesList[i], copyHTML);
    }
    document.getElementById("notes-form-root").appendChild(copyHTML);
}

function createNoteWidget(note, notesForm) {
    var notesWidget = notesForm.querySelector("#notes-widget");
    var notesWidgetTemplate = document.getElementById("note-widget-template").content;
    var copyHTML = document.importNode(notesWidgetTemplate, true);
    var noteWidgetElement = copyHTML.querySelector("#note-widget");
    noteWidgetElement.id = "note-widget-" + note.name;
    noteWidgetElement.querySelector("#deleteButton").setAttribute("onclick","deleteNote('" + note.name + "')");

    copyHTML.querySelector(".card-header").innerHTML = note.name;
    copyHTML.querySelector(".card-text").innerHTML = note.text;
    notesWidget.appendChild(copyHTML);
}

function deleteNotesForm() {
    var node= document.getElementById("notes-form-root");
    while (node.firstChild) {
        node.removeChild(node.firstChild);
    }
}
function logOut() {
    deleteNotesForm();
    createLoginForm();
}
function createNoteForm() {
    var noteFormTemplate = document.getElementById("add-note-form-template").content;
    var copyHTML = document.importNode(noteFormTemplate, true);
    document.getElementById("add-notes-form").appendChild(copyHTML);
}
function addNote(){
    var notesFormRootElement = document.getElementById("notes-form-root");
    var note = new Object();
    note.name = notesFormRootElement.querySelector("#addNoteName").value;
    note.text = notesFormRootElement.querySelector("#addNoteText").value;

    var xhrAddNote = new XMLHttpRequest();
     xhrAddNote.onreadystatechange = function() {
         if (xhrAddNote.readyState == 4 && xhrAddNote.status == 200) {
            var noteResponse = JSON.parse(xhrAddNote.responseText);
            createNoteWidget(noteResponse, notesFormRootElement)
            deleteAddNoteForm();
         }
         else if (xhrAddNote.readyState == 4) {
           if(xhrAddNote.status == 409){
                alert('This note name already exist');
            }else {
                alert('Unknown error on the server');
            }
         }
     };
     var path = "/note?userName=" + currentUser.userName + "&userPassword=" + currentUser.password;
     xhrAddNote.open('post', path);
     xhrAddNote.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
     xhrAddNote.send(JSON.stringify(note));
}
function deleteAddNoteForm(){
    var node= document.getElementById("add-notes-form");
    while (node.firstChild) {
        node.removeChild(node.firstChild);
    }
}
function deleteNote(noteName) {
      var xhrDeleteNote = new XMLHttpRequest();
      xhrDeleteNote.onreadystatechange = function() {
        if (xhrDeleteNote.readyState == 4 && xhrDeleteNote.status == 200) {
            deleteNoteForm(noteName);
        } else if (xhrDeleteNote.readyState == 4) {
            alert('Unknown error on the server');
        }
      }
      var path = "/note?userName=" + currentUser.userName + "&userPassword=" + currentUser.password + "&noteName=" + noteName;
      xhrDeleteNote.open('delete', path);
      xhrDeleteNote.send();

}
function deleteNoteForm(noteName) {
    var parent= document.getElementById("notes-form-root");
    var noteElement = parent.querySelector("#note-widget-" + noteName);
    while (noteElement.firstChild) {
        noteElement.removeChild(noteElement.firstChild);
    }
    noteElement.remove();
}


createLoginForm();


