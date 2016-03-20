package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.etsmtl.applets.etsmobile.ui.activity.NotesDetailsActivity;
import ca.etsmtl.applets.etsmobile2.R;

public class NoteAdapter extends ArrayAdapter<NotesSessionItem> {

	private Context context;

	public NoteAdapter(Context context, int resource, NotesSessionItem[] notesSession) {
		super(context, resource, notesSession);
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewHolder holder;
		if (view != null) {
			holder = (ViewHolder) view.getTag();
		} else {
			view = inflater.inflate(R.layout.row_note_menu, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}
		final NotesSessionItem notesSession = getItem(position);
		holder.tvNoteMenuSession.setText(notesSession.sessionName);
		holder.gridview.setAdapter(notesSession.arrayAdapter);
		holder.gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				SessionCoteItem sessionCote = (SessionCoteItem) notesSession.arrayAdapter.getItem(position);
				String cote = " ";
				if (sessionCote.cote != null) {
					cote = sessionCote.cote;
				}


                Intent i = new Intent(context, NotesDetailsActivity.class);
                i.putExtra("sigle", sessionCote.sigle);
                i.putExtra("sessionName", notesSession.sessionName);
				i.putExtra("abrege", notesSession.abrege);
                i.putExtra("cote", cote);
                i.putExtra("groupe", sessionCote.groupe);
                i.putExtra("titreCours", sessionCote.titreCours);
                context.startActivity(i);
			}
		});
		return view;
	}

	static class ViewHolder {
		@Bind(R.id.row_note_menu_session_text)
		TextView tvNoteMenuSession;
		@Bind(R.id.row_note_menu_gridview)
		GridView gridview;
		public ViewHolder(View view){
			ButterKnife.bind(this, view);
		}
	}

}
