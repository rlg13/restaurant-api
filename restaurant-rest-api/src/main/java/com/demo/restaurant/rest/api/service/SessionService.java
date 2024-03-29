package com.demo.restaurant.rest.api.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.demo.restaurant.rest.api.controller.beans.UserRest;
import com.demo.restaurant.rest.api.exceptions.NoDataFoundException;
import com.demo.restaurant.rest.api.model.Session;
import com.demo.restaurant.rest.api.model.Users;
import com.demo.restaurant.rest.api.repository.SessionRepository;
import com.demo.restaurant.rest.api.utils.ProjectVars;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@Service
public class SessionService {

	private SessionRepository sessionRepository;

	private ProjectVars projectVars;

	public Session createSession(UserRest user) {
		Session session = new Session();
		Users userModel = new Users();

		userModel.setId(user.getId());
		session.setUser(userModel);
		session.setExpirationDate(nextDate());
		session = sessionRepository.save(session);

		return session;

	}

	public void invalidateSession(Long userID) {
		Users user = new Users();

		user.setId(userID);
		List<Session> sessionActive = sessionRepository.findByUser(user);
		for (Session temp : sessionActive) {
			sessionRepository.deleteById(temp.getId());
		}
	}

	public void refreshSession(@NonNull UUID sessionId) throws NoDataFoundException {
		try {
			Session session = sessionRepository.findById(sessionId).orElseThrow();
			session.setExpirationDate(nextDate());
			sessionRepository.save(session);
		} catch (NoSuchElementException e) {
			throw new NoDataFoundException(NoDataFoundException.SESSION_NOT_FOUND);
		}

	}

	public UserRest getUserBySession(@NonNull UUID sessionId) throws NoDataFoundException {
		UserRest user;

		try {
			Session session = sessionRepository.findById(sessionId).orElseThrow();
			user = new UserRest();
			BeanUtils.copyProperties(session.getUser(), user);

		} catch (NoSuchElementException e) {
			throw new NoDataFoundException(NoDataFoundException.SESSION_NOT_FOUND);
		}

		return user;
	}

	public boolean validateSession(@NonNull UUID sessionId) throws NoDataFoundException {

		try {
			Session session = sessionRepository.findById(sessionId).orElseThrow();
			Calendar now = Calendar.getInstance();
			return now.getTime().before(session.getExpirationDate());
		} catch (NoSuchElementException e) {
			throw new NoDataFoundException(NoDataFoundException.SESSION_NOT_FOUND);
		}

	}

	private Date nextDate() {
		Calendar now = Calendar.getInstance();

		now.add(Calendar.MILLISECOND, projectVars.getMilisecondsToExpireSession());

		return now.getTime();
	}

}
