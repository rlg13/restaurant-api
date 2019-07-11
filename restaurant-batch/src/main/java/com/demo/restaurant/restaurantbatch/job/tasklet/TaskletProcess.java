package com.demo.restaurant.restaurantbatch.job.tasklet;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.restaurant.rest.api.controller.beans.UserRest;
import com.demo.restaurant.restaurantbatch.job.service.CallApiService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TaskletProcess implements Tasklet, StepExecutionListener{

	private StepExecution stepExecution;
	
	@Autowired
	private CallApiService callApiService;
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.debug("Ejecutando Job");
		UserRest sessionStablished = callApiService.stablishSession();
		
		
		
		return RepeatStatus.FINISHED;
	}

}