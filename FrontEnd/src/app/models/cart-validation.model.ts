export class ItemValidado {
  productId: number;
  titulo: string;
  imagenUrl: string;
  precioUnitario: number;
  cantidadSolicitada: number;
  cantidadDisponible: number;
  valido: boolean;
  mensaje: string;

  constructor(init?: Partial<ItemValidado>) {
    this.productId = init?.productId ?? 0;
    this.titulo = init?.titulo ?? '';
    this.imagenUrl = init?.imagenUrl ?? '';
    this.precioUnitario = init?.precioUnitario ?? 0;
    this.cantidadSolicitada = init?.cantidadSolicitada ?? 0;
    this.cantidadDisponible = init?.cantidadDisponible ?? 0;
    this.valido = init?.valido ?? false;
    this.mensaje = init?.mensaje ?? '';
  }
}

export class CarritoValidacionResponse {
  itemsValidados: ItemValidado[];
  totalCalculado: number;
  carritoCompletoValido: boolean;

  constructor(init?: Partial<CarritoValidacionResponse>) {
    this.itemsValidados = (init?.itemsValidados ?? []).map(i => new ItemValidado(i));
    this.totalCalculado = init?.totalCalculado ?? 0;
    this.carritoCompletoValido = init?.carritoCompletoValido ?? false;
  }
}
