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

export class CategoryDTO {
  id: number | null;
  nombre: string;
  descripcion: string;

  constructor(init?: Partial<CategoryDTO>) {
    this.id = init?.id ?? null;
    this.nombre = init?.nombre ?? '';
    this.descripcion = init?.descripcion ?? '';
  }
}
