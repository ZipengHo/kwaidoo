package gpf.dc.basic.expimp;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class VisitedContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6536406207570135837L;
	Set<String> visitedActions = new LinkedHashSet<>();
	Set<String> visitedPdfs = new LinkedHashSet<>();
	Set<String> roleCodes = new LinkedHashSet<>();
	boolean onlyExportView = false;

	public Set<String> getVisitedActions() {
		return visitedActions;
	}

	public VisitedContext setVisitedActions(Set<String> visitedActions) {
		this.visitedActions = visitedActions;
		return this;
	}

	public Set<String> getVisitedPdfs() {
		return visitedPdfs;
	}

	public VisitedContext setVisitedPdfs(Set<String> visitedPdfs) {
		this.visitedPdfs = visitedPdfs;
		return this;
	}

	public Set<String> getRoleCodes() {
		return roleCodes;
	}

	public VisitedContext setRoleCodes(Set<String> roleCodes) {
		this.roleCodes = roleCodes;
		return this;
	}

	public boolean isOnlyExportView() {
		return onlyExportView;
	}

	public VisitedContext setOnlyExportView(boolean onlyExportView) {
		this.onlyExportView = onlyExportView;
		return this;
	}
}
