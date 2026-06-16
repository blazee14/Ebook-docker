package com.api.capas.domain.dto;

import java.math.BigDecimal;

public class ProductoMasVendidoDTO {
	private Integer id;
	private String isbn;
	private String titulo;
	private String autor;
	private String imagenUrl;
	private BigDecimal precio;
	private Long cantidadTotalVendida;

	public ProductoMasVendidoDTO(Integer id, String isbn, String titulo, String autor, String imagenUrl,
			BigDecimal precio, Long cantidadTotalVendida) {
		this.id = id;
		this.isbn = isbn;
		this.titulo = titulo;
		this.autor = autor;
		this.imagenUrl = imagenUrl;
		this.precio = precio;
		this.cantidadTotalVendida = cantidadTotalVendida;
	}

	public ProductoMasVendidoDTO() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public Long getCantidadTotalVendida() {
		return cantidadTotalVendida;
	}

	public void setCantidadTotalVendida(Long cantidadTotalVendida) {
		this.cantidadTotalVendida = cantidadTotalVendida;
	}
}