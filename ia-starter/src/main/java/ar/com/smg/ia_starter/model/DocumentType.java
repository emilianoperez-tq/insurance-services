package ar.com.smg.ia_starter.model;

public enum DocumentType {
  RECETA_MEDICA("Receta Médica"),
  RESULTADO_LABORATORIO("Resultado de Laboratorio"),
  INFORME_MEDICO("Informe Médico"),
  RADIOGRAFIA("Radiografía"),
  FACTURA_MEDICA("Factura Médica"),
  HISTORIA_CLINICA("Historia Clínica"),
  ORDEN_MEDICA("Orden Médica"),
  CONSENTIMIENTO_INFORMADO("Consentimiento Informado"),
  OTRO("Otro Documento"),
  NO_CLASIFICABLE("No Clasificable");

  private final String displayName;

  DocumentType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
