/**
 * Copyright (c) 2015 Mustafa DUMLUPINAR, mdumlupinar@gmail.com
 *
 * This file is part of seyhan project.
 *
 * seyhan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.search.NovaposhtaCargoTransSearchParam;
import play.data.format.Formats.DateTime;
import play.data.validation.Constraints;
import utils.DateUtils;
import utils.ModelHelper;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import enums.Module;
import enums.Right;

@Entity
/**
 * @author mdpinar
 */
public class NovaposhtaCargoTrans extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Constraints.Required
	@Constraints.MaxLength(30)
	public String registrationNo;

	@Constraints.Required
	@DateTime(pattern = "dd/MM/yyyy")
	public Date transDate = new Date();

	public Double cargoValue;
	public Double money;
	
	@Column(name = "_return")
	public Double return_;
	public Double total;

	@Constraints.MaxLength(100)
	public String description;

	@ManyToOne
	public NovaposhtaCargo cargo;

	public Integer transYear;
	public String transMonth;

	public static Page<NovaposhtaCargoTrans> page(NovaposhtaCargoTransSearchParam searchParam) {
		ExpressionList<NovaposhtaCargoTrans> expList = ModelHelper.getExpressionList(Right.NOVAPOSHTA_KARGO_HAREKETLERI.module);

		if (searchParam.fullText != null && !searchParam.fullText.isEmpty()) {
			Expr.or(
					Expr.like("transDate", "%" + searchParam.fullText + "%"),
					Expr.like("registrationNo", "%" + searchParam.fullText + "%")
			);
		} else {
			if (searchParam.registrationNo != null && !searchParam.registrationNo.isEmpty()) {
				expList.eq("registrationNo", searchParam.registrationNo);
			}
			if (searchParam.startDate != null) {
				expList.ge("transDate", searchParam.startDate);
			}
			if (searchParam.endDate != null) {
				expList.le("transDate", searchParam.endDate);
			}
			if (searchParam.cargo != null && searchParam.cargo.id != null) {
				expList.eq("cargo", searchParam.cargo);
			}
		}

		return ModelHelper.getPage(Right.NOVAPOSHTA_KARGO_HAREKETLERI, expList, searchParam);
	}

	public static NovaposhtaCargoTrans findById(Integer id) {
		return ModelHelper.findById(Module.novaposhta, id);
	}

	@Override
	public Right getAuditRight() {
		return Right.NOVAPOSHTA_KARGO_HAREKETLERI;
	}

	@Override
	public String getAuditDescription() {
		return DateUtils.formatDateForDB(this.transDate)
				+ (this.registrationNo != null && !this.registrationNo.isEmpty() ? " - " + this.registrationNo : "");
	}

}
