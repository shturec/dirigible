/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.git.JGitConnector;
import org.eclipse.dirigible.repository.local.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalBaseException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

/**
 * The Git based implementation of {@link IRepository}
 */
public class GitMasterRepository extends FileSystemRepository implements IMasterRepository {

	private String gitLocation;

	private String gitUser;

	private String gitPassword;

	private String gitBranch;

	public GitMasterRepository(String user, String targetFolder, String gitLocation, String gitUser, String gitPassword, String gitBranch)
			throws LocalBaseException {
		super(user, targetFolder);
		this.gitLocation = gitLocation;
		this.gitUser = gitUser;
		this.gitPassword = gitPassword;
		this.gitBranch = gitBranch;
	}

	public void reset() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		JGitConnector.cloneRepository(new File(getRepositoryPath()), getGitLocation(), getGitUser(), getGitPassword());
		JGitConnector gitConnector = new JGitConnector(JGitConnector.getRepository(getRepositoryPath()));
		gitConnector.hardReset();
	}

	public String getGitLocation() {
		return gitLocation;
	}

	public String getGitUser() {
		return gitUser;
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public String getGitBranch() {
		return gitBranch;
	}

}
