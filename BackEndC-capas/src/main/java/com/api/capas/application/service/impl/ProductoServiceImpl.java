package com.api.capas.application.service.impl;

import com.api.capas.domain.dto.ProductoDTO;
import com.api.capas.domain.dto.ProductoResponseDTO;
import com.api.capas.domain.dto.ProductoMasVendidoDTO;
import com.api.capas.config.ResourceNotFoundException;
import com.api.capas.infrastructure.persistence.entities.Categoria;
import com.api.capas.infrastructure.persistence.entities.Producto;
import com.api.capas.infrastructure.persistence.repositories.CategoriaRepository;
import com.api.capas.infrastructure.persistence.repositories.DetallePedidoRepository; // Importar DetallePedidoRepository
import com.api.capas.infrastructure.persistence.repositories.ProductoRepository;
import com.api.capas.application.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               DetallePedidoRepository detallePedidoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    private ProductoResponseDTO convertToDto(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setIsbn(producto.getIsbn());
        dto.setTitulo(producto.getTitulo());
        dto.setAutor(producto.getAutor());
        dto.setSinopsis(producto.getSinopsis());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setImagenUrl(producto.getImagenUrl());
        if (producto.getCategoria() != null) {
            dto.setCategoria(new ProductoResponseDTO.CategoriaSimpleDTO(
                    producto.getCategoria().getId(),
                    producto.getCategoria().getNombre()
            ));
        }
        dto.setCreatedAt(producto.getCreatedAt());
        dto.setUpdatedAt(producto.getUpdatedAt());
        dto.setIdCategoria(producto.getCategoria() != null ? producto.getCategoria().getId() : null);
        return dto;
    }

    @Override
    public List<ProductoResponseDTO> getAllProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponseDTO getProductoById(Integer id) {
        Integer productoId = Objects.requireNonNull(id, "id must not be null");
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));
        return convertToDto(producto);
    }

    @Override
    public List<ProductoResponseDTO> getProductosByTitulo(String titulo) {
        return productoRepository.findByTituloContainingIgnoreCase(titulo).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDTO> getProductosByAutor(String autor) {
        return productoRepository.findByAutorContainingIgnoreCase(autor).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDTO> getProductosByCategoria(Integer idCategoria) {
        Integer categoriaId = Objects.requireNonNull(idCategoria, "idCategoria must not be null");
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId);
        }
        return productoRepository.findByCategoria_Id(categoriaId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDTO> buscarProductos(String termino) {
        return productoRepository.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(termino, termino)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponseDTO saveProducto(ProductoDTO productoDTO) {
        Objects.requireNonNull(productoDTO, "productoDTO must not be null");
        Producto producto;

        if (productoDTO.getId() != null) {
            Integer productoId = Objects.requireNonNull(productoDTO.getId(), "id must not be null");
            producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

            if (!producto.getIsbn().equals(productoDTO.getIsbn())) {
                Optional<Producto> productoConMismoIsbn = productoRepository.findByIsbn(productoDTO.getIsbn());
                if (productoConMismoIsbn.isPresent() && !productoConMismoIsbn.get().getId().equals(productoId)) {
                    throw new IllegalArgumentException("Ya existe otro producto con el ISBN: " + productoDTO.getIsbn());
                }
            }
        } else {
            if (productoRepository.findByIsbn(productoDTO.getIsbn()).isPresent()) {
                throw new IllegalArgumentException("Ya existe un producto con el ISBN: " + productoDTO.getIsbn());
            }
            producto = new Producto();
        }

        producto.setIsbn(productoDTO.getIsbn());
        producto.setTitulo(productoDTO.getTitulo());
        producto.setAutor(productoDTO.getAutor());
        producto.setSinopsis(productoDTO.getSinopsis());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());
        producto.setImagenUrl(productoDTO.getImagenUrl());

        Integer categoriaId = Objects.requireNonNull(productoDTO.getIdCategoria(), "idCategoria must not be null");
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId));
        producto.setCategoria(categoria);

        Producto savedProducto = productoRepository.save(producto);
        return convertToDto(savedProducto);
    }

    @Override
    @Transactional // <-- ¡AÑADIDA ESTA ANOTACIÓN PARA ASEGURAR ATOMICIDAD EN LA VERIFICACIÓN!
    public void deleteProducto(Integer id) {
        Integer productoId = Objects.requireNonNull(id, "id must not be null");
        // Primero, verifica si el producto existe
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        // --- ¡LÓGICA DE VERIFICACIÓN DE DEPENDENCIAS APLICADA ANTES DE LA ELIMINACIÓN! ---
        // Verifica si hay detalles de pedido asociados a este producto
        if (detallePedidoRepository.countByProductoId(productoId) > 0) {
            // Si hay detalles de pedido, lanzamos una excepción con un mensaje claro
            throw new IllegalArgumentException("No se puede eliminar el libro '" + producto.getTitulo() + "' porque tiene ventas asociadas en pedidos.");
        }
        // --------------------------------------------------------------------------------

        // Si no hay ventas asociadas, procede a eliminar el producto
        productoRepository.deleteById(productoId);
    }

    @Override
    public List<ProductoMasVendidoDTO> getTopSellingProducts(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("El límite para los productos más vendidos debe ser mayor que cero.");
        }
        List<ProductoMasVendidoDTO> topProducts = detallePedidoRepository.findTopSellingProductsRaw();
        return topProducts.stream()
                          .limit(limit)
                          .collect(Collectors.toList());
    }
}