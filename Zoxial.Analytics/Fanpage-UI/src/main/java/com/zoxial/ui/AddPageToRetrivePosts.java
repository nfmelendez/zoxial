package com.zoxial.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

public class AddPageToRetrivePosts extends WebPage {

	private String id = "", name = "";

	public AddPageToRetrivePosts() {
		Form f = new Form("form");
		this.add(f);
		f.add(new TextField("id", new PropertyModel(this, "id")));
		f.add(new TextField("name", new PropertyModel(this, "name")));
		Button button = new Button("btn") {
			public void onSubmit() {
				insertPage();
				insertInBlogPoster(new String(id), new String(name));
			}

		};
		f.add(button);
	}

	public void insertPage() {
		BasicDataSource datasource = Analytics.datasource;

		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = datasource.getConnection();
			stmt = conn
					.prepareStatement("INSERT INTO `facebook_pages`  (id, name) VALUES (?, ?)");
			stmt.setString(1, id);
			stmt.setString(2, name);
			stmt.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}

	public void insertInBlogPoster(String id, String name) {
		BasicDataSource datasource = Analytics.datasource;

		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = datasource.getConnection();
			stmt = conn
					.prepareStatement("INSERT INTO blog_poster.page_to_post  (id, name) VALUES (?, ?)");
			stmt.setString(1, id);
			stmt.setString(2, name);
			stmt.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}
}
