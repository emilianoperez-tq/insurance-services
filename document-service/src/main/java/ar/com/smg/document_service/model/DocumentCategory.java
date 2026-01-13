package ar.com.smg.document_service.model;

public enum DocumentCategory {

  FACTURAS("facturas/"),
  CONTRATOS("contratos/"),
  REPORTES("reportes/"),
  OTROS("otros/");

  private final String prefijo;

  /**
   * Constructor del enum
   * @param prefijo Prefijo de la categoría para usar en S3
   */
  DocumentCategory(String prefijo) {
    this.prefijo = prefijo;
  }

  /**
   * Obtiene el prefijo de la categoría
   * @return Prefijo de S3 (ej: "facturas/")
   */
  public String getPrefijo() {
    return prefijo;
  }

  /**
   * Convierte un String a DocumentCategory
   * Si el String no es válido o es null, retorna OTROS
   *
   * @param categoria String con el nombre de la categoría
   * @return DocumentCategory correspondiente o OTROS por defecto
   */
  public static DocumentCategory fromString(String categoria) {
    if (categoria == null || categoria.trim().isEmpty()) {
      return OTROS;
    }

    try {
      return DocumentCategory.valueOf(categoria.toUpperCase().trim());
    } catch (IllegalArgumentException e) {
      return OTROS;
    }
  }

  /**
   * Valida si un String es una categoría válida
   *
   * @param categoria String a validar
   * @return true si es válida, false en caso contrario
   */
  public static boolean esValida(String categoria) {
    if (categoria == null || categoria.trim().isEmpty()) {
      return false;
    }

    try {
      DocumentCategory.valueOf(categoria.toUpperCase().trim());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Obtiene todas las categorías disponibles como array de Strings
   *
   * @return Array con los nombres de todas las categorías
   */
  public static String[] obtenerNombres() {
    DocumentCategory[] categorias = DocumentCategory.values();
    String[] nombres = new String[categorias.length];

    for (int i = 0; i < categorias.length; i++) {
      nombres[i] = categorias[i].name();
    }

    return nombres;
  }

  /**
   * Determina la categoría basándose en el tipo de archivo
   * Útil para migración automática de archivos
   *
   * @param nombreArchivo Nombre del archivo
   * @return Categoría sugerida basada en el nombre/extensión
   */
  public static DocumentCategory determinarPorNombre(String nombreArchivo) {
    if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
      return OTROS;
    }

    String nombre = nombreArchivo.toLowerCase();

    // Por nombre del archivo
    if (nombre.contains("factura") || nombre.contains("invoice")) {
      return FACTURAS;
    } else if (nombre.contains("contrato") || nombre.contains("contract")) {
      return CONTRATOS;
    } else if (nombre.contains("reporte") || nombre.contains("report")) {
      return REPORTES;
    }

    // Por extensión
    if (nombre.endsWith(".pdf")) {
      return REPORTES;
    } else if (nombre.endsWith(".docx") || nombre.endsWith(".doc")) {
      return CONTRATOS;
    } else if (nombre.endsWith(".xlsx") || nombre.endsWith(".xls")) {
      return REPORTES;
    }

    return OTROS;
  }

  /**
   * Override del toString para obtener el nombre en minúsculas
   *
   * @return Nombre de la categoría en minúsculas
   */
  @Override
  public String toString() {
    return this.name().toLowerCase();
  }
}
