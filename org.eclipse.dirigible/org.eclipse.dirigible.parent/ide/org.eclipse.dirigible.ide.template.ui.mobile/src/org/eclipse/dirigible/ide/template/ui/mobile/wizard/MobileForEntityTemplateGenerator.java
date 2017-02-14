package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.table.TableColumn;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class MobileForEntityTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "MOBILE_APP_FOR_ENTITY_GENERATOR"; //$NON-NLS-1$
	private MobileForEntityTemplateModel model;
	
	private static final String REST_SERVICE_ROOT_JS = "../../js"; //$NON-NLS-1$

	private static final String PARAMETER_PAGE_TITLE = "pageTitle"; //$NON-NLS-1$
	private static final String PARAMETER_TABLE_NAME = "tableName"; //$NON-NLS-1$
	private static final String PARAMETER_DEPENDENT_COLUMN = "dependentColumn"; //$NON-NLS-1$
	private static final String PARAMETER_TABLE_COLUMNS = "tableColumns"; //$NON-NLS-1$
	private static final String PARAMETER_SERVICE_FILE_NAME = "serviceFileName"; //$NON-NLS-1$
	private static final String PARAMETER_ENTITY_NAME = "entityName"; //$NON-NLS-1$
	private static final String PARAMETER_PRIMARY_KEY = "primaryKey"; //$NON-NLS-1$
	
	public MobileForEntityTemplateGenerator(MobileForEntityTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = super.prepareParameters();
		parameters.put(PARAMETER_PAGE_TITLE, model.getPageTitle());
		parameters.put(PARAMETER_TABLE_NAME, model.getTableName());
		parameters.put(PARAMETER_DEPENDENT_COLUMN, model.getDependentColumn());
		parameters.put(PARAMETER_TABLE_COLUMNS, model.getTableColumns());
		parameters.put(PARAMETER_SERVICE_FILE_NAME, generateServiceFileName());
		parameters.put(PARAMETER_ENTITY_NAME, getEntityName());
		parameters.put(PARAMETER_PRIMARY_KEY, getPrimaryKey());
		return parameters;
	}
	
	private Object getPrimaryKey() {
		TableColumn[] columns = model.getTableColumns();
		TableColumn primaryKey = null;
		for (TableColumn column : columns) {
			if (column.isKey()) {
				primaryKey = column;
			}
		}
		if (primaryKey == null) {
//			logger.error(String.format("There is no primary key in table %s, which can produce errornous artifacts", model.getTableName()));
			return null;
		}
		return primaryKey;
	}

	protected String getEntityName() {
		return "data"; //$NON-NLS-1$
	}

	protected Object generateServiceFileName() {
		// /project1/ScriptingServices/te1.entity
		String result = "";
		if (model.getSourceFile() != null) {
			IFile source = model.getSourceFile();
			String entityPath = source.getFullPath().toString();

			int index = entityPath.indexOf(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES);
			if (index >= 0) {
				result = entityPath.substring(index + ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES.length());
			} else {
				result = entityPath;
			}
			result = result.replace(source.getFileExtension(), ""); //$NON-NLS-1$
			result += "js"; //$NON-NLS-1$
		} else {
			result = model.getServiceEndpoint();
		}

		return REST_SERVICE_ROOT_JS + result;
	}


	@Override
	protected GenerationModel getModel() {
		return this.model;
	}

	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}

}