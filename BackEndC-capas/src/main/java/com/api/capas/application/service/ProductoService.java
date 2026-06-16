package com.api.capas.application.service;

import com.api.capas.domain.dto.ProductoDTO;
import com.api.capas.domain.dto.ProductoResponseDTO;
import com.api.capas.domain.dto.ProductoMasVendidoDTO;

import java.util.List;

public interface ProductoService {

    List<ProductoResponseDTO> getAllProductos();
    ProductoResponseDTO getProductoById(Integer id);
    List<ProductoResponseDTO> getProductosByTitulo(String titulo);
    List<ProductoResponseDTO> getProductosByAutor(String autor);
    List<ProductoResponseDTO> getProductosByCategoria(Integer idCategoria);
    List<ProductoResponseDTO> buscarProductos(String termino);

    ProductoResponseDTO saveProducto(ProductoDTO productoDTO);

    void deleteProducto(Integer id);

    List<ProductoMasVendidoDTO> getTopSellingProducts(int limit); 
}