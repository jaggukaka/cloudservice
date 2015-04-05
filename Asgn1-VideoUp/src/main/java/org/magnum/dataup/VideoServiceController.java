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
package org.magnum.dataup;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
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
import org.springframework.web.multipart.MultipartFile;



@Controller
public class VideoServiceController {

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	
	@Autowired
	VideoFileManager fileManager;
	
	
	@RequestMapping(value ="/video", method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVedioList() {
		return fileManager.getVideoList();
	}
	
	@RequestMapping(value ="/video", method=RequestMethod.POST) 
	public @ResponseBody Video pushVideoDetails(@RequestBody Video video, HttpServletRequest request) {
		Video videoGenerated = fileManager.createNewVideo(video, request);
		return videoGenerated;
	}
	
	@RequestMapping(value="/video/{id}/data", method=RequestMethod.POST)
	public ResponseEntity<?> pushVideoBinary (@PathVariable("id") long id, @RequestParam("data") MultipartFile
            data) {
		
		Video video = fileManager.getVideoFromId(id);
		if (video == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		VideoStatus vs = new VideoStatus(VideoState.READY);
		try {
		InputStream videoStream = data.getInputStream();
		fileManager.saveVideoData(video, videoStream);
		} catch (Exception ex) {
			 vs = new VideoStatus(VideoState.ERROR);
			return new ResponseEntity<VideoStatus>(vs, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<VideoStatus>(vs, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/video/{id}/data", method=RequestMethod.GET)
	public ResponseEntity<?> getVideoBinary(@PathVariable("id") long id, HttpServletResponse response) {
		Video video = fileManager.getVideoFromId(id);
		if (video == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		try {
			OutputStream outputStream = response.getOutputStream();
		fileManager.copyVideoData(video, outputStream);
		return new ResponseEntity<>(HttpStatus.OK);
		} catch (IOException ex) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
}
