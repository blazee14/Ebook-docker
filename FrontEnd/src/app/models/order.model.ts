/**
 * Define las estructuras de datos relacionadas con los Pedidos.
 */

export class CartItemRequest {
  productId: number;
  cantidad: number;

  constructor(init?: Partial<CartItemRequest>) {
    this.productId = init?.productId ?? 0;
    this.cantidad = init?.cantidad ?? 0;
  }
}

export class ConfirmarCompraRequest {
  idMetodoPago: number;
  items: CartItemRequest[];
  paymentMethodId?: string;

  constructor(init?: Partial<ConfirmarCompraRequest>) {
    this.idMetodoPago = init?.idMetodoPago ?? 0;
    this.items = (init?.items ?? []).map(i => new CartItemRequest(i));
    this.paymentMethodId = init?.paymentMethodId;
  }
}

export class OrderDetailResponse {
  id: number;
  idProducto: number;
  tituloProducto: string;
  imagenProductoUrl: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;

  constructor(init?: Partial<OrderDetailResponse>) {
    this.id = init?.id ?? 0;
    this.idProducto = init?.idProducto ?? 0;
    this.tituloProducto = init?.tituloProducto ?? '';
    this.imagenProductoUrl = init?.imagenProductoUrl ?? '';
    this.cantidad = init?.cantidad ?? 0;
    this.precioUnitario = init?.precioUnitario ?? 0;
    this.subtotal = init?.subtotal ?? 0;
  }
}

export class PedidoResponse {
  id: number;
  idUsuario: number;
  nombreUsuario: string;
  metodoPagoNombre: string;
  estado: string;
  total: number;
  fecha: string;
  createdAt: string;
  updatedAt: string;
  detalles: OrderDetailResponse[];
  clientSecret?: string;
  paymentIntentId?: string;
  paymentStatus?: string;

  constructor(init?: Partial<PedidoResponse>) {
    this.id = init?.id ?? 0;
    this.idUsuario = init?.idUsuario ?? 0;
    this.nombreUsuario = init?.nombreUsuario ?? '';
    this.metodoPagoNombre = init?.metodoPagoNombre ?? '';
    this.estado = init?.estado ?? '';
    this.total = init?.total ?? 0;
    this.fecha = init?.fecha ?? '';
    this.createdAt = init?.createdAt ?? '';
    this.updatedAt = init?.updatedAt ?? '';
    this.detalles = (init?.detalles ?? []).map(d => new OrderDetailResponse(d));
    this.clientSecret = init?.clientSecret;
    this.paymentIntentId = init?.paymentIntentId;
    this.paymentStatus = init?.paymentStatus;
  }
}
