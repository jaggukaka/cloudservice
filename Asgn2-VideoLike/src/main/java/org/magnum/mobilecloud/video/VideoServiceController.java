/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VideoServiceController {

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it to
	 * something other than "AnEmptyController"
	 * 
	 * 
	 * ________ ________ ________ ________ ___ ___ ___ ________ ___ __ |\
	 * ____\|\ __ \|\ __ \|\ ___ \ |\ \ |\ \|\ \|\ ____\|\ \|\ \ \ \ \___|\ \
	 * \|\ \ \ \|\ \ \ \_|\ \ \ \ \ \ \ \\\ \ \ \___|\ \ \/ /|_ \ \ \ __\ \ \\\
	 * \ \ \\\ \ \ \ \\ \ \ \ \ \ \ \\\ \ \ \ \ \ ___ \ \ \ \|\ \ \ \\\ \ \ \\\
	 * \ \ \_\\ \ \ \ \____\ \ \\\ \ \ \____\ \ \\ \ \ \ \_______\ \_______\
	 * \_______\ \_______\ \ \_______\ \_______\ \_______\ \__\\ \__\
	 * \|_______|\|_______|\|_______|\|_______|
	 * \|_______|\|_______|\|_______|\|__| \|__|
	 * 
	 * 
	 */

	@Autowired
	VideoRepository videoRepository;

//	@Autowired
//	UserRepository userRepository;

	@RequestMapping(value = "/video", method = RequestMethod.GET)
	public @ResponseBody
	Collection<Video> getVedioList() {
		return (Collection<Video>) videoRepository.findAll();
	}

	@RequestMapping(value = "/video", method = RequestMethod.POST)
	public @ResponseBody
	Video pushVideoDetails(@RequestBody Video video, HttpServletRequest request) {
		return videoRepository.save(video);
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getVideoDetails(@PathVariable("id") long id) {

		Video video = videoRepository.findOne(id);
		if (video == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Video>(video, HttpStatus.OK);

	}

	@RequestMapping(value = "/video/{id}/like", method = RequestMethod.POST)
	public ResponseEntity<?> likeVideo(@PathVariable("id") long id, Principal p) {
		String username = p.getName();
		//User user = userRepository.findByUsername(username);
		
		Video video = videoRepository.findOne(id);
		if (video == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (isLikedByUser(video, username)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		updateLikeByUser(video, username);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequestMapping(value = "/video/{id}/unlike", method = RequestMethod.POST)
	public ResponseEntity<?> unlikeVideo(@PathVariable("id") long id,
			Principal p) {
		String username = p.getName();
		//User user = userRepository.findByUsername(username);
		Video video = videoRepository.findOne(id);
		if (video == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (!isLikedByUser(video, username)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		updateUnLikeByUser(video, username);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequestMapping(value = "/video/{id}/likedby", method = RequestMethod.GET)
	public ResponseEntity<?> videoLikedBy(@PathVariable("id") long id) {

		Video video = videoRepository.findOne(id);
		if (video == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<String>>(video.getUsers(),
				HttpStatus.OK);

	}

	@RequestMapping(value="/video/search/findByName", method = RequestMethod.GET)
	public @ResponseBody Collection<Video> videosByTitle(@RequestParam("title") String title) {
		return videoRepository.findByName(title);
	}

	@RequestMapping(value="/video/search/findByDurationLessThan", method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(
			@RequestParam("duration") long duration) {
		return videoRepository.findByDurationLessThan(duration);
	}

	private void updateLikeByUser(Video video, String user) {
		// TODO Auto-generated method stub

		video.getUsers().add(user);
		video.setLikes(video.getLikes() + 1);
		videoRepository.save(video);
	}

	private void updateUnLikeByUser(Video video, String user) {
		// TODO Auto-generated method stub

		video.getUsers().remove(user);
		video.setLikes(video.getLikes() - 1);
		videoRepository.save(video);
	}

	private boolean isLikedByUser(Video video, String user) {
		// TODO Auto-generated method stub

		Collection<String> users = video.getUsers();
		if (users.contains(user)) {
			return true;
		}
		return false;
	}

}
