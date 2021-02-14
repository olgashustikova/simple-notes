package com.olga.shust.nevernote;

import com.olga.shust.nevernote.data.Note;
import com.olga.shust.nevernote.data.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
public class NeverNoteApplication {
	List<User> usersList = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(NeverNoteApplication.class, args);
	}

	@PostMapping("/login") 
	public ResponseEntity<User> addUser(@RequestBody User user) {
		System.out.println("New incoming user");
		System.out.println("Name: " + user.userName);

		if(user.userName == null || user.userName.isEmpty() || user.password == null || user.password.isEmpty()) {
			return new ResponseEntity<>(user,HttpStatus.BAD_REQUEST);
		}
		User currentUser = findUser(user.userName);
		if (currentUser == null) {
			usersList.add(user);
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
		if (user.password.equals(currentUser.password)) {
			return new ResponseEntity<>(currentUser, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(user, HttpStatus.FORBIDDEN);
		}

	}

	public User findUser(String name){
		for(int i=0;i<usersList.size(); i++){
			if(name.equals(usersList.get(i).userName)) {
				return usersList.get(i);
			}
		}
		return null;
	}

	@PostMapping("/note")
	public ResponseEntity<Note> addNote(@RequestBody Note note, String userName, String userPassword) {
		System.out.println("New incoming note");
		System.out.println("Name: " + note.name);
		System.out.println("Text: " + note.text);
		if(note.name == null || note.name.isEmpty() || note.text == null || note.text.isEmpty()) {
			return new ResponseEntity<>(note,HttpStatus.BAD_REQUEST);
		}
		User currentUser = findUser(userName);
		if(!currentUser.password.equals(userPassword)){
			return new ResponseEntity<>(note, HttpStatus.FORBIDDEN);
		}
		if(currentUser == null){
			return new ResponseEntity<>(note, HttpStatus.NOT_FOUND);
		}
		if(checkIfNoteAlreadyExists(currentUser, note)) {
			return new ResponseEntity<>(note, HttpStatus.CONFLICT);
		}
		currentUser.notesList.add(note);
		return new ResponseEntity<>(note, HttpStatus.OK);
	}

	public Boolean checkIfNoteAlreadyExists(User user, Note note){
		for (int i = 0; i < user.notesList.size(); i++) {
			if (note.name.equals(user.notesList.get(i).name)) {
				return true;
			}
		}
		return false;
	}

	@PostMapping("/updatepassword")
	public ResponseEntity<User> updatePassword(@RequestBody User user, String newPassword) {
		if(user == null || user.userName == null || user.password == null || newPassword == null ||user.userName.isEmpty() || user.password.isEmpty()){
			return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
		}
		User currentUser = findUser(user.userName);
		if(currentUser==null){
			return new ResponseEntity<>(user, HttpStatus.FORBIDDEN);
		}
		if(currentUser.password.equals(user.password)) {
			currentUser.password = newPassword;
			return new ResponseEntity<>(currentUser, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(user, HttpStatus.FORBIDDEN);
		}

	}
	@DeleteMapping("/note")
	public ResponseEntity<Note> deleteNote(String noteName, String userName, String userPassword) {
		if (userName == null || userName.isEmpty() || userPassword == null || userPassword.isEmpty() || noteName.isEmpty() || noteName == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		User currentUser = findUser(userName);
		if(!currentUser.password.equals(userPassword)){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Note currentNote = deleteNote(noteName, currentUser.notesList);
		if (currentNote == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(currentNote, HttpStatus.OK);

		}

	}
	public Note deleteNote(String noteName, List<Note> noteList){
		for(int i=0;i<noteList.size(); i++){
			if(noteName.equals(noteList.get(i).name)) {
				Note noteForRemove = noteList.get(i);
				noteList.remove(i);
				return noteForRemove;
			}
		}
		return null;
	}




}
