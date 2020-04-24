/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.web;

import java.time.LocalDate;
import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetStatus;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.exceptions.DuplicatedPetNameException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@RequestMapping("/owners/{ownerId}")
public class PetController {

	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";

	private final PetService petService;
	private final OwnerService ownerService;

	@Autowired
	public PetController(final PetService petService, final OwnerService ownerService) {
		this.petService = petService;
		this.ownerService = ownerService;
	}

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return this.petService.findPetTypes();
	}

	@ModelAttribute("status")
	public Collection<PetStatus> populatePetStatus() {
		return this.petService.findPetStatus();
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") final int ownerId) {
		return this.ownerService.findOwnerById(ownerId);
	}

	/*
	 * @ModelAttribute("pet") public Pet findPet(@PathVariable("petId") Integer
	 * petId) { Pet result=null; if(petId!=null)
	 * result=this.clinicService.findPetById(petId); else result=new Pet(); return
	 * result; }
	 */

	@InitBinder("owner")
	public void initOwnerBinder(final WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@InitBinder("pet")
	public void initPetBinder(final WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
	}

	@GetMapping(value = "/pets/new")
	public String initCreationForm(final Owner owner, final ModelMap model) {
		Pet pet = new Pet();
		owner.addPet(pet);
		model.put("pet", pet);
		return PetController.VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping(value = "/pets/new")
	public String processCreationForm(final Owner owner, @Valid final Pet pet, final BindingResult result,
			final ModelMap model) {
		if (pet.getBirthDate().isAfter(LocalDate.now())) {
			result.rejectValue("birthDate", "Incorrect bithdate", "The bithdate date must be in the past");
		}
		if (result.hasErrors()) {
			model.put("pet", pet);
			return PetController.VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		} else {
			try {
				owner.addPet(pet);
				this.petService.savePet(pet);
			} catch (DuplicatedPetNameException ex) {
				result.rejectValue("name", "duplicate", "already exists");
				return PetController.VIEWS_PETS_CREATE_OR_UPDATE_FORM;
			}
			return "redirect:/owners/{ownerId}";
		}
	}

	@GetMapping(value = "/pets/{petId}/edit")
	public String initUpdateForm(@PathVariable("petId") final int petId, final ModelMap model) {
		Pet pet = this.petService.findPetById(petId);
		model.put("pet", pet);
		return PetController.VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	/**
	 *
	 * @param pet
	 * @param result
	 * @param petId
	 * @param model
	 * @param owner
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/pets/{petId}/edit")
	public String processUpdateForm(@Valid final Pet pet, final BindingResult result, final Owner owner,
			@PathVariable("petId") final int petId, final ModelMap model) {
		if (pet.getBirthDate().isAfter(LocalDate.now())) {
			result.rejectValue("birthDate", "Incorrect bithdate", "The bithdate date must be in the past");
		}
		if (result.hasErrors()) {
			model.put("pet", pet);
			return PetController.VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		} else {
			Pet petToUpdate = this.petService.findPetById(petId);
			BeanUtils.copyProperties(pet, petToUpdate, "id", "owner", "visits", "stays", "status");
			try {
				this.petService.savePet(petToUpdate);
			} catch (DuplicatedPetNameException ex) {
				result.rejectValue("name", "duplicate", "already exists");
				return PetController.VIEWS_PETS_CREATE_OR_UPDATE_FORM;
			}
			return "redirect:/owners/{ownerId}";
		}
	}

	@GetMapping(value = "/pets/{petId}/delete")
	public String initDeleteForm(@PathVariable("petId") int petId, Owner owner) {
		Pet pet = this.petService.findPetById(petId);

		if (pet.getStatus().getName().equals("HEALTHY")) {
			owner.removePet(pet);
			this.petService.deletePet(pet);
			return "redirect:/owners/{ownerId}";
		}else {
			return "/exception";
		}
	}

}
