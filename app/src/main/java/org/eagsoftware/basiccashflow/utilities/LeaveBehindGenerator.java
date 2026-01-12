package org.eagsoftware.basiccashflow.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Questa classe permette di creare un leave behind layout per lo swipe laterale di un elemento
 * dell'interfaccia utente implementato con un {@link androidx.recyclerview.widget.ItemTouchHelper }.
 * <h4>Utilizzo</h4>
 * <ul>
 *     <li>Creare un oggetto {@link LeaveBehindGenerator} all'interno dell'implementazione dell'interfaccia
 *     {@link androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback}.
 *     </li>
 *     <li>Istanziare l'oggetto all'interno del metodo <code>onChildDraw</code> della suddetta
 *     interfaccia.</li>
 *     <li>Chiamare il metodo {@link #generate(Canvas, RecyclerView.ViewHolder, float)} all'interno del
 *     metodo <code>onChildDraw</code>.</li>
 *     <li>Chiamare il metodo {@link #destroy()} all'interno del metodo <code>onChildDrawOver</code></li>
 * </ul>
 */
public class LeaveBehindGenerator {
    private final RecyclerView recyclerView;
    private final Context context;
    final Integer layoutDXid;
    final Integer layoutSXid;

    @SuppressWarnings("unused")
    private View layoutDX;
    @SuppressWarnings("unused")
    private View layoutSX;

    /**
     * Costruttore della classe
     * @param recyclerView recuperabile dal parametro del metodo <code>onChildDraw</code>
     * @param context il contesto dell'activity o del fragment
     * @param layoutSXid l'id del layout leave behind da mostrare allo swipe verso sinistra
     * @param layoutDXid l'id del layout leave behind da mostrare allo swipe verso destra
     */
    public LeaveBehindGenerator(RecyclerView recyclerView, Context context, @Nullable Integer layoutSXid,
                                @Nullable Integer layoutDXid) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.layoutSXid = layoutSXid;
        this.layoutDXid = layoutDXid;
    }

    /** Metodo per la generazione del layout
     * @param canvas recuperabile dal parametro del metodo <code>onChildDraw</code>
     * @param viewHolder recuperabile dal parametro del metodo <code>onChildDraw</code>
     * @param dX recuperabile dal parametro del metodo <code>onChildDraw</code>
     */
    public void generate(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX){
        View viewItem = viewHolder.itemView;
        canvas.drawColor(Color.TRANSPARENT);

        // Disegna il layout della swipe dx
        if (layoutSXid != null && dX < 0 ) {
            drawLayout(canvas, viewItem, layoutSX, layoutSXid, dX);
        }
        // Disegna il layout della swipe sx
        else if (layoutDXid != null && dX > 0 ) {
            drawLayout(canvas, viewItem, layoutDX, layoutDXid, dX);
        }
    }

    /** Metodo per la distruzione del layout */
    public void destroy(){
        if (layoutDX != null) {
            layoutDX.layout(0, 0, 0, 0); // Reset the layout to avoid drawing it again
        }
    }

    /*
     *
     * METODI INTERNI */

    private void drawLayout(Canvas canvas, View viewItem, View layout, int layoutID, float translationX){
        // Gonfiaggio del layout se non ancora fatto
        if (layout == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            layout = inflater.inflate(layoutID, null);
        }

        // Misura il layout
        layout.measure(
                View.MeasureSpec.makeMeasureSpec(viewItem.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(viewItem.getHeight(), View.MeasureSpec.EXACTLY)
        );

        // Imposta le dimensioni del layout
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());

        // Ottiene le posizioni assolute del viewHolder e della recyclerView
        Rect rectHolder = new Rect();
        Rect rectRcy = new Rect();
        viewItem.getGlobalVisibleRect(rectHolder);
        recyclerView.getGlobalVisibleRect(rectRcy);

        // Trasla il canvas sulla base delle dimensioni ottenute precedentemente
        canvas.save();
        canvas.translate(0, rectHolder.top - rectRcy.top);
        layout.draw(canvas);
        canvas.restore();

        // Aggiorna la posizione dell'item della lista
        viewItem.setTranslationX(translationX);
    }
}
