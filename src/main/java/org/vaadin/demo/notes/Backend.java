package org.vaadin.demo.notes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

// Simplified example persistency layer implemented with relational database
public class Backend {

	static SimpleJDBCConnectionPool connectionPool = setupConnectionPoolAndCreateMockupDatabase();
	static public final String titleId = "TITLE";
	static public final String textId = "TEXT";

	public static Container getNotesContainer() {
		try {
			if (connectionPool == null)
				return null;
			return new SQLContainer(new TableQuery("notes", connectionPool));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void commitChanges(Container modifiedConteiner) {
		try {
			((SQLContainer) modifiedConteiner).commit();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static SimpleJDBCConnectionPool setupConnectionPoolAndCreateMockupDatabase() {
		try {
			SimpleJDBCConnectionPool pool = new SimpleJDBCConnectionPool(
					"org.h2.Driver", "jdbc:h2:mem:notes", "sa", "sa");
			Connection c = pool.reserveConnection();
			try {
				c.createStatement().executeQuery("select * from notes");
			} catch (SQLException e) {
				createTable(c);
				initDatabaseWithRandomData(c);
			} finally {
				pool.releaseConnection(c);
			}
			return pool;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Database contains just one table: notes with id, title and text columns
	private static void createTable(Connection c) throws SQLException {
		c.createStatement().executeUpdate(
				"create table notes(id long primary key, " + titleId
						+ " varchar(100), " + textId + " varchar(4000))");
	}

	private static void initDatabaseWithRandomData(Connection connection)
			throws SQLException {
		for (int i = 0; i < 500; i++) {
			PreparedStatement p = connection
					.prepareStatement("insert into notes values (?,?,?)");
			p.setLong(1, i);
			p.setString(2, generateRandomTitle((int) (Math.random() * 5 + 1)));
			p.setString(3, generateRandomHTML((int) (Math.random() * 250 + 1)));
			p.executeUpdate();
		}
		connection.commit();
	}

	private static String generateRandomHTML(int minimumNumberOfWords) {
		StringBuffer sb = new StringBuffer();
		while (minimumNumberOfWords > 0) {
			sb.append("<h2>");
			int len = (int) (Math.random() * 4) + 1;
			sb.append(generateRandomTitle(len));
			sb.append("</h2>");
			minimumNumberOfWords -= len;
			int paragraphs = 1 + (int) (Math.random() * 3);
			while (paragraphs-- > 0 && minimumNumberOfWords > 0) {
				sb.append("<p>");
				len = (int) (Math.random() * 40) + 3;
				sb.append(generateRandomText(len));
				sb.append("</p>");
				minimumNumberOfWords -= len;
			}
		}
		return sb.toString();
	}

	private static String generateRandomTitle(int numberOfWords) {
		StringBuffer sb = new StringBuffer();
		int len = (int) (Math.random() * 4) + 1;
		sb.append(generateRandomWords(len, true));
		while (--numberOfWords > 0) {
			len = (int) (Math.random() * 4) + 1;
			sb.append(' ');
			sb.append(generateRandomWords(len, false));
		}
		return sb.toString();
	}

	private static String generateRandomWords(int numberOfParts,
			boolean capitalized) {
		String[] part = { "ger", "ma", "isa", "app", "le", "ni", "ke", "mic",
				"ro", "soft", "wa", "re", "lo", "gi", "is", "acc", "el", "tes",
				"la", "ko", "ni", "ka", "so", "ny", "mi", "nol", "ta", "pa",
				"na", "so", "nic", "sa", "les", "for", "ce" };
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < numberOfParts; i++) {
			String p = part[(int) (Math.random() * part.length)];
			if (i == 0 && capitalized)
				p = Character.toUpperCase(p.charAt(0)) + p.substring(1);
			sb.append(p);
		}
		return sb.toString();
	}

	private static String generateRandomText(int words) {
		StringBuffer sb = new StringBuffer();
		int sentenceWordsLeft = 0;
		while (words-- > 0) {
			if (sb.length() > 0)
				sb.append(' ');
			if (sentenceWordsLeft == 0 && words > 0) {
				sentenceWordsLeft = (int) (Math.random() * 15);
				sb.append(generateRandomWords(1 + (int) (Math.random() * 3),
						true));
			} else {
				sentenceWordsLeft--;
				sb.append(generateRandomWords(1 + (int) (Math.random() * 3),
						false));
				if (words > 0 && sentenceWordsLeft > 2 && Math.random() < 0.2)
					sb.append(',');
				else if (sentenceWordsLeft == 0 || words == 0)
					sb.append('.');
			}
		}
		return sb.toString();
	}

}
