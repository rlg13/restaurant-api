package com.demo.restaurant.rest.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.demo.restaurant.rest.api.controller.beans.DishRest;
import com.demo.restaurant.rest.api.exceptions.AlreadyExistsException;
import com.demo.restaurant.rest.api.exceptions.NoDataFoundException;
import com.demo.restaurant.rest.api.model.DishCatalog;
import com.demo.restaurant.rest.api.repository.DishCatalogRepository;
import com.demo.restaurant.rest.api.types.DishType;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@Service
public class DishService {

	private DishCatalogRepository dishCatalogRepository;

	public List<DishRest> getDishesByType(DishType type) {
		List<DishRest> dishesByType = new ArrayList<>();

		for (DishCatalog temp : dishCatalogRepository.findByType(type)) {
			DishRest dest = new DishRest();
			BeanUtils.copyProperties(temp, dest);
			dishesByType.add(dest);
		}

		return dishesByType;
	}

	public DishRest createDish(DishRest dish) throws AlreadyExistsException {
		DishCatalog newDish = new DishCatalog();

		BeanUtils.copyProperties(dish, newDish);
		try {
			newDish = dishCatalogRepository.save(newDish);
			dish.setId(newDish.getId());
			return dish;
		} catch (DataIntegrityViolationException exp) {
			throw new AlreadyExistsException(AlreadyExistsException.DISH_EXISTS);
		}

	}

	public DishRest getDish(@NonNull Long idDish) throws NoDataFoundException {
		DishRest dish = new DishRest();

		try {
			DishCatalog dishItem = dishCatalogRepository.findById(idDish).orElseThrow();
			BeanUtils.copyProperties(dishItem, dish);
		} catch (NoSuchElementException e) {
			throw new NoDataFoundException(NoDataFoundException.DISH_NOT_FOUND);
		}

		return dish;
	}

}
