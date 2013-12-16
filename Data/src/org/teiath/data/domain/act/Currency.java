package org.teiath.data.domain.act;

import javax.persistence.*;

@Entity
@Table(name = "act_currencies")
public class Currency {

	@Id
	@Column(name = "currency_id")
	@SequenceGenerator(name = "currencies_seq", sequenceName = "act_currencies_currency_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currencies_seq")
	private Integer id;

	@Column(name = "currency_code", length = 50, nullable = false)
	private String code;
	@Column(name = "currency_name", length = 200, nullable = false)
	private String name;
	@Column(name = "currency_default", nullable = true)
	private boolean defaultCurrency;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(boolean defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == Currency.class && this.id
				.equals(((Currency) obj).getId());
	}
}
