package org.springframework.samples.petclinic.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "discounts")
public class Discount extends NamedEntity{

	@NotEmpty
	@Column(name = "percentage")
	private Double percentage;
	
	@Column(name = "start_date")        
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate startDate;
	
	@Column(name = "finish_date")        
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate finishDate;
	
	
}