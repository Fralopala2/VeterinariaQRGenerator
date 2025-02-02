
package com.mycompany.veterinariaqrgenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 *
 * @author NOX
 */

public class VeterinariaQRGenerator extends JFrame {
    private JTextField nombreMascotaField, propietarioField, loteVacunaField, veterinarioField, proximaVacunaField,
                       nombreMedicamentoField, dosisField, frecuenciaField, duracionField;
    private JComboBox<String> tipoRegistroCombo, tipoVacunaCombo;
    private JTextArea previewArea;
    private final int QR_SIZE = 300;

    public VeterinariaQRGenerator() {
        setTitle("Generador QR - Vacunas y Medicamentos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(crearPanelBasicos());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Usando java.awt.Dimension
        mainPanel.add(crearPanelEspecificos());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Usando java.awt.Dimension
        mainPanel.add(crearPanelBotones());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Usando java.awt.Dimension
        mainPanel.add(new JLabel("Vista previa del contenido:"));
        mainPanel.add(new JScrollPane(previewArea = new JTextArea(4, 40)));

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelBasicos() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Nombre de la Mascota:"));
        panel.add(nombreMascotaField = new JTextField());
        panel.add(new JLabel("Propietario:"));
        panel.add(propietarioField = new JTextField());
        panel.add(new JLabel("Tipo de Registro:"));
        panel.add(tipoRegistroCombo = new JComboBox<>(new String[]{"Vacuna", "Medicamento"}));
        return panel;
    }

    private JPanel crearPanelEspecificos() {
        JPanel panel = new JPanel(new CardLayout());
        panel.add(crearPanelVacunas(), "Vacuna");
        panel.add(crearPanelMedicamentos(), "Medicamento");
        tipoRegistroCombo.addActionListener(e -> ((CardLayout) panel.getLayout()).show(panel, (String) tipoRegistroCombo.getSelectedItem()));
        return panel;
    }

    private JPanel crearPanelVacunas() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Tipo de Vacuna:"));
        panel.add(tipoVacunaCombo = new JComboBox<>(new String[]{"Rabia", "Parvovirus", "Moquillo", "Hepatitis infecciosa canina", "Leptospirosis", "Triple felina", "Leucemia felina"}));
        panel.add(new JLabel("Lote de Vacuna:"));
        panel.add(loteVacunaField = new JTextField());
        panel.add(new JLabel("Veterinario:"));
        panel.add(veterinarioField = new JTextField());
        panel.add(new JLabel("Proxima Vacuna:"));
        panel.add(proximaVacunaField = new JTextField());
        return panel;
    }

    private JPanel crearPanelMedicamentos() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Nombre del Medicamento:"));
        panel.add(nombreMedicamentoField = new JTextField());
        panel.add(new JLabel("Dosis:"));
        panel.add(dosisField = new JTextField());
        panel.add(new JLabel("Frecuencia:"));
        panel.add(frecuenciaField = new JTextField());
        panel.add(new JLabel("Duracion del Tratamiento:"));
        panel.add(duracionField = new JTextField());
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton generarButton = new JButton("Generar QR");
        JButton limpiarButton = new JButton("Limpiar");
        generarButton.addActionListener(e -> generarQR());
        limpiarButton.addActionListener(e -> limpiarCampos());
        panel.add(generarButton);
        panel.add(limpiarButton);
        return panel;
    }

    private void generarQR() {
        if (validarCampos()) {
            try {
                String contenido = generarContenidoQR();
                previewArea.setText(contenido);

                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
                BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

                String nombreArchivo = "QR_" + nombreMascotaField.getText().replaceAll("\\s+", "_") + "_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".png";
                ImageIO.write(qrImage, "PNG", new File(nombreArchivo));

                mostrarImagenQR(qrImage, nombreArchivo);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al generar el código QR: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarImagenQR(BufferedImage qrImage, String nombreArchivo) {
        JDialog dialog = new JDialog(this, "Codigo QR Generado", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JLabel(new ImageIcon(qrImage)), BorderLayout.CENTER);
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        JOptionPane.showMessageDialog(this, "Codigo QR guardado como: " + nombreArchivo, "Exito", JOptionPane.INFORMATION_MESSAGE);
    }

    private String generarContenidoQR() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("Clinica Veterinaria - Registro\n========================\n");
        contenido.append("Mascota: ").append(nombreMascotaField.getText()).append("\n");
        contenido.append("Propietario: ").append(propietarioField.getText()).append("\n");
        contenido.append("Fecha: ").append(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");

        if (tipoRegistroCombo.getSelectedItem().equals("Vacuna")) {
            contenido.append("\nDETALLES DE VACUNACION\n");
            contenido.append("Tipo de Vacuna: ").append(tipoVacunaCombo.getSelectedItem()).append("\n");
            contenido.append("Lote: ").append(loteVacunaField.getText()).append("\n");
            contenido.append("Veterinario: ").append(veterinarioField.getText()).append("\n");
            contenido.append("Proxima Vacuna: ").append(proximaVacunaField.getText()).append("\n");
        } else {
            contenido.append("\nDETALLES DE MEDICAMENTO\n");
            contenido.append("Medicamento: ").append(nombreMedicamentoField.getText()).append("\n");
            contenido.append("Dosis: ").append(dosisField.getText()).append("\n");
            contenido.append("Frecuencia: ").append(frecuenciaField.getText()).append("\n");
            contenido.append("Duracion: ").append(duracionField.getText()).append("\n");
        }
        contenido.append("========================");
        return contenido.toString();
    }

    private boolean validarCampos() {
        if (nombreMascotaField.getText().trim().isEmpty() || propietarioField.getText().trim().isEmpty()) {
            mostrarAdvertencia("Por favor, complete los campos basicos");
            return false;
        }

        if (tipoRegistroCombo.getSelectedItem().equals("Vacuna")) {
            if (loteVacunaField.getText().trim().isEmpty() || veterinarioField.getText().trim().isEmpty() || proximaVacunaField.getText().trim().isEmpty()) {
                mostrarAdvertencia("Por favor, complete todos los campos de la vacuna");
                return false;
            }
        } else {
            if (nombreMedicamentoField.getText().trim().isEmpty() || dosisField.getText().trim().isEmpty() || frecuenciaField.getText().trim().isEmpty() || duracionField.getText().trim().isEmpty()) {
                mostrarAdvertencia("Por favor, complete todos los campos del medicamento");
                return false;
            }
        }
        return true;
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
    }

    private void limpiarCampos() {
        nombreMascotaField.setText("");
        propietarioField.setText("");
        tipoRegistroCombo.setSelectedIndex(0);
        loteVacunaField.setText("");
        veterinarioField.setText("");
        proximaVacunaField.setText("");
        tipoVacunaCombo.setSelectedIndex(0);
        nombreMedicamentoField.setText("");
        dosisField.setText("");
        frecuenciaField.setText("");
        duracionField.setText("");
        previewArea.setText("");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new VeterinariaQRGenerator().setVisible(true));
    }
}
