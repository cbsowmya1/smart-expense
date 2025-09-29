package wip;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartexpense.workflow.dto.SpendByCategoryItem;
import com.smartexpense.workflow.dto.TopVendorItem;
import com.smartexpense.workflow.dto.ViolationsRateItem;

@Service
public class ReportService {
    public List<SpendByCategoryItem> spendByCategory(String fromMonth, String toMonth) {
        return List.of(); // TODO replace with native query or view
    }
    public List<ViolationsRateItem> violationsRate(String fromMonth, String toMonth) {
        return List.of();
    }
    public List<TopVendorItem> topVendors(String fromMonth, String toMonth) {
        return List.of();
    }
}
