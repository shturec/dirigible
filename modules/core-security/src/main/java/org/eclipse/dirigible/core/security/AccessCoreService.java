package org.eclipse.dirigible.core.security;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.squle.Squle;

@Singleton
public class AccessCoreService implements ISecurityCoreService {
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private PersistenceManager<RoleDefinition> rolesPersistenceManager;
	
	@Inject
	private PersistenceManager<AccessDefinition> accessPersistenceManager;
	
	boolean rolesTableExists = false;
	
	boolean accessTableExists = false;
	
	// Roles
	
	public void createRole(String name, String description) throws AccessException {
		RoleDefinition roleDefinition = new RoleDefinition();
		roleDefinition.setName(name);
		roleDefinition.setDescription(description);
		roleDefinition.setCreatedBy(UserFacade.getName());
		roleDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkRolesTable(connection);
				rolesPersistenceManager.insert(connection, roleDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	public RoleDefinition getRole(String name) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkRolesTable(connection);
				return rolesPersistenceManager.find(connection, RoleDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	public void removeRole(String name) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkRolesTable(connection);
				rolesPersistenceManager.delete(connection, RoleDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	public void updateRole(String name, String description) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkRolesTable(connection);
				RoleDefinition roleDefinition = getRole(name);
				roleDefinition.setDescription(description);
				rolesPersistenceManager.update(connection, roleDefinition, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	public List<RoleDefinition> getRoles() throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkRolesTable(connection);
				return rolesPersistenceManager.findAll(connection, RoleDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	private void checkRolesTable(Connection connection) {
		if (!rolesTableExists) {
			if (!rolesPersistenceManager.tableExists(connection, RoleDefinition.class)) {
				rolesPersistenceManager.tableCreate(connection, RoleDefinition.class);				
			}
			rolesTableExists = true;
		}
	}
	
	
	// Access
	
	public void createAccess(String location, String role, String description) throws AccessException {
		AccessDefinition accessDefinition = new AccessDefinition();
		accessDefinition.setLocation(location);
		accessDefinition.setRole(role);
		accessDefinition.setDescription(description);
		accessDefinition.setCreatedBy(UserFacade.getName());
		accessDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
				accessPersistenceManager.insert(connection, accessDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	public AccessDefinition getAccess(String id) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
				return accessPersistenceManager.find(connection, AccessDefinition.class, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	public void removeAccess(String id) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
				accessPersistenceManager.delete(connection, AccessDefinition.class, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	public void updateAccess(String id, String location, String role, String description) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
				AccessDefinition accessDefinition = getAccess(id);
				accessDefinition.setLocation(location);
				accessDefinition.setRole(role);
				accessDefinition.setDescription(description);
				accessPersistenceManager.update(connection, accessDefinition, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	public List<AccessDefinition> getAccessDefinitions() throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
				return accessPersistenceManager.findAll(connection, AccessDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	public List<AccessDefinition> getAccessByLocation(String location) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
				String sql = Squle.getNative(connection)
						.select()
						.column("*")
						.from("DIRIGIBLE_SECURITY_ACCESS")
						.where("ACCESS_LOCATION = ?").toString();
				return accessPersistenceManager.query(connection, AccessDefinition.class, sql, Arrays.asList(location));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	

	private void checkAccessTable(Connection connection) {
		if (!accessTableExists) {
			if (!accessPersistenceManager.tableExists(connection, AccessDefinition.class)) {
				accessPersistenceManager.tableCreate(connection, AccessDefinition.class);				
			}
			accessTableExists = true;
		}
	}

}
