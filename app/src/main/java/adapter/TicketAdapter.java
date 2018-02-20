package adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kums.lotto10.R;

import org.w3c.dom.Text;

import java.util.List;

import pojo.TicketNumber;

/**
 * Created by Kums on 2/20/2018.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyHolder> {
    private List<TicketNumber> ticketNumbers;

    public TicketAdapter(List<TicketNumber> ticketNumbers) {
        this.ticketNumbers = ticketNumbers;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_design,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        TicketNumber ticket=ticketNumbers.get(position);
        holder.ticketNumber.setText(ticket.getTicketNumber());
    }

    @Override
    public int getItemCount() {
        return ticketNumbers.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView ticketNumber;
        public MyHolder(View itemView) {
            super(itemView);
            ticketNumber=(TextView)itemView.findViewById(R.id.ticket_number);
        }
    }
}
