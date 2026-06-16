export class ProductoMasVendidoDTO {
  id: number;
  isbn: string;
  titulo: string;
  autor: string;
  imagenUrl: string;
  precio: number;
  cantidadTotalVendida: number;

  constructor(init?: Partial<ProductoMasVendidoDTO>) {
    this.id = init?.id ?? 0;
    this.isbn = init?.isbn ?? '';
    this.titulo = init?.titulo ?? '';
    this.autor = init?.autor ?? '';
    this.imagenUrl = init?.imagenUrl ?? '';
    this.precio = init?.precio ?? 0;
    this.cantidadTotalVendida = init?.cantidadTotalVendida ?? 0;
  }
}

export class Category {
  id: number;
  nombre: string;
  descripcion?: string;
  createdAt?: string;
  updatedAt?: string;

  constructor(init?: Partial<Category>) {
    this.id = init?.id ?? 0;
    this.nombre = init?.nombre ?? '';
    this.descripcion = init?.descripcion;
    this.createdAt = init?.createdAt;
    this.updatedAt = init?.updatedAt;
  }
}

export class Product {
  id: number;
  isbn: string;
  titulo: string;
  autor: string;
  sinopsis: string;
  precio: number;
  stock: number;
  imagenUrl: string;
  categoria: Category;
  createdAt?: string;
  updatedAt?: string;

  constructor(init?: Partial<Product>) {
    this.id = init?.id ?? 0;
    this.isbn = init?.isbn ?? '';
    this.titulo = init?.titulo ?? '';
    this.autor = init?.autor ?? '';
    this.sinopsis = init?.sinopsis ?? '';
    this.precio = init?.precio ?? 0;
    this.stock = init?.stock ?? 0;
    this.imagenUrl = init?.imagenUrl ?? '';
    this.categoria = init?.categoria ? new Category(init?.categoria) : new Category();
    this.createdAt = init?.createdAt;
    this.updatedAt = init?.updatedAt;
  }
}

export class ProductDTO {
  id: number | null;
  isbn: string;
  titulo: string;
  autor: string;
  sinopsis: string;
  precio: number;
  stock: number;
  imagenUrl: string;
  idCategoria: number | null;

  constructor(init?: Partial<ProductDTO>) {
    this.id = init?.id ?? null;
    this.isbn = init?.isbn ?? '';
    this.titulo = init?.titulo ?? '';
    this.autor = init?.autor ?? '';
    this.sinopsis = init?.sinopsis ?? '';
    this.precio = init?.precio ?? 0;
    this.stock = init?.stock ?? 0;
    this.imagenUrl = init?.imagenUrl ?? '';
    this.idCategoria = init?.idCategoria ?? null;
  }
}

export class CartItem extends Product {
  quantity: number;

  constructor(init?: Partial<CartItem>) {
    super(init);
    this.quantity = init?.quantity ?? 0;
  }
}
