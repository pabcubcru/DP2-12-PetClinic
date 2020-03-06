package org.springframework.samples.petclinic.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "shops")
public class Shop extends NamedEntity {

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "shop_products", joinColumns = @JoinColumn(name = "shop_id"),
	inverseJoinColumns = @JoinColumn(name = "product_id"))
	private Set<Product> products;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "shop_orders", joinColumns = @JoinColumn(name = "shop_id"),
	inverseJoinColumns = @JoinColumn(name = "order_id"))
	private Set<Order> orders;
	
	protected Set<Product> getProductsInternal() {
		if (this.products == null) {
			this.products = new HashSet<>();
		}
		return this.products;
	}
	
	protected void setProductsInternal(Set<Product> products) {
		this.products = products;
	}
	
	public int getNumberOfProducts() {
		return getProductsInternal().size();
	}
	
	public void addProduct(Product product) {
		getProductsInternal().add(product);
	}
	
	protected Set<Order> getOrdersInternal() {
		if (this.orders == null) {
			this.orders = new HashSet<>();
		}
		return this.orders;
	}
	
	protected void setOrdersInternal(Set<Order> orders) {
		this.orders = orders;
	}
	
	public int getNumberOfOrders() {
		return getOrdersInternal().size();
	}
	
	public void addOrder(Order order) {
		getOrdersInternal().add(order);
	}
}