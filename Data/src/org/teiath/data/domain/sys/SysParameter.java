package org.teiath.data.domain.sys;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sys_parameters")
public class SysParameter implements Serializable {
	@Id
	@Column(name = "param_id")
	@SequenceGenerator(name = "params_seq", sequenceName = "sys_parameters_param_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "params_seq")
	private Integer id;
	@Column(name = "levenshtein_percent", nullable = false)
	private Integer levenshteinPercent;
	@Column(name = "aggregator_enabled", nullable = false)
	private boolean aggregatorEnabled;

	public SysParameter() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLevenshteinPercent() {
		return levenshteinPercent;
	}

	public void setLevenshteinPercent(Integer levenshteinPercent) {
		this.levenshteinPercent = levenshteinPercent;
	}

	public boolean isAggregatorEnabled() {
		return aggregatorEnabled;
	}

	public void setAggregatorEnabled(boolean aggregatorEnabled) {
		this.aggregatorEnabled = aggregatorEnabled;
	}
}
