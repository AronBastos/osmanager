package util;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.*;

/**
 * Classe de teste para verificar a conexão com o banco de dados
 * Utiliza tanto JDBC puro quanto JNDI DataSource
 */
public class ConnectionTest {

    // Teste de conexão usando DataSource via JNDI

    public static void testDataSource() {
        try {
            // Obter contexto JNDI
            InitialContext ctx = new InitialContext();

            // Buscar o DataSource configurado no Tomcat
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/osmanager");

            // Obter conexão
            try (Connection conn = ds.getConnection()) {
                System.out.println("✅ Conexão via DataSource estabelecida com sucesso!");
                System.out.println("   Banco: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("   Versão: " + conn.getMetaData().getDatabaseProductVersion());
            }

        } catch (NamingException | SQLException e) {
            System.err.println("❌ Erro ao conectar via DataSource: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Teste de conexão usando JDBC DriverManager (para referência)
     */
    public static void testDriverManager() {
        String url = "jdbc:mysql://localhost:3306/osmanager?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "senha123";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Conexão via DriverManager estabelecida com sucesso!");

            // Testar uma consulta simples
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tecnicos")) {

                if (rs.next()) {
                    System.out.println("   Total de técnicos: " + rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao conectar via DriverManager: " + e.getMessage());
        }
    }

    /**
     * Método de teste de consulta usando JDBC preparado
     */
    public static void testConsultaPreparada() {
        String sql = "SELECT id, nome, email, especialidade FROM tecnicos WHERE ativo = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, true);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n📋 Lista de Técnicos Ativos:");
                System.out.println("=" .repeat(60));

                while (rs.next()) {
                    System.out.printf("ID: %d | Nome: %-20s | Email: %-25s | Especialidade: %s%n",
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("especialidade")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro na consulta: " + e.getMessage());
        }
    }

    private static Connection getConnection() throws SQLException {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/osmanager");
            return ds.getConnection();
        } catch (NamingException e) {
            throw new SQLException("Erro ao obter DataSource", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== TESTE DE CONEXÃO COM BANCO DE DADOS ===\n");

        testDataSource();
        System.out.println();

        testDriverManager();
        System.out.println();

        testConsultaPreparada();
    }
}
