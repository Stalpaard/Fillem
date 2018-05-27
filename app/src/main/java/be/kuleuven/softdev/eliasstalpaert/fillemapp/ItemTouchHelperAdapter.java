package be.kuleuven.softdev.eliasstalpaert.fillemapp;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}